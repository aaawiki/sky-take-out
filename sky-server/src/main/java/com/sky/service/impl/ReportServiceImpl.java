package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据统计业务实现
 */
@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WorkspaceService workspaceService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 统计指定时间区间内的营业额数据
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDateTime begin, LocalDateTime end) {
        //构造日期列表
        List<LocalDate> dateList = buildDateList(begin, end);

        //遍历日期，查询每天的营业额
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : dateList) {
            Map<String, Object> map = new HashMap<>();
            //营业额只统计已完成的订单
            map.put("status", Orders.COMPLETED);
            map.put("begin", LocalDateTime.of(date, LocalTime.MIN));
            map.put("end", LocalDateTime.of(date, LocalTime.MAX));
            Double turnover = orderMapper.sumByMap(map);
            turnoverList.add(turnover == null ? 0.0 : turnover);
        }

        return TurnoverReportVO.builder()
                .dateList(dateList.stream().map(LocalDate::toString).collect(Collectors.joining(",")))
                .turnoverList(turnoverList.stream().map(String::valueOf).collect(Collectors.joining(",")))
                .build();
    }

    /**
     * 统计指定时间区间内的用户数据
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO getUserStatistics(LocalDateTime begin, LocalDateTime end) {
        List<LocalDate> dateList = buildDateList(begin, end);

        List<Integer> newUserList = new ArrayList<>();
        List<Integer> totalUserList = new ArrayList<>();
        for (LocalDate date : dateList) {
            //查询截止到当天结束时的用户总量
            Map<String, Object> map = new HashMap<>();
            map.put("end", LocalDateTime.of(date, LocalTime.MAX));
            Integer totalUser = userMapper.countByMap(map);
            totalUserList.add(totalUser == null ? 0 : totalUser);

            //查询当天新增用户数
            map.put("begin", LocalDateTime.of(date, LocalTime.MIN));
            Integer newUser = userMapper.countByMap(map);
            newUserList.add(newUser == null ? 0 : newUser);
        }

        return UserReportVO.builder()
                .dateList(dateList.stream().map(LocalDate::toString).collect(Collectors.joining(",")))
                .totalUserList(totalUserList.stream().map(String::valueOf).collect(Collectors.joining(",")))
                .newUserList(newUserList.stream().map(String::valueOf).collect(Collectors.joining(",")))
                .build();
    }

    /**
     * 统计指定时间区间内的订单数据
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO getOrderStatistics(LocalDateTime begin, LocalDateTime end) {
        List<LocalDate> dateList = buildDateList(begin, end);

        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();
        for (LocalDate date : dateList) {
            Map<String, Object> map = new HashMap<>();
            map.put("begin", LocalDateTime.of(date, LocalTime.MIN));
            map.put("end", LocalDateTime.of(date, LocalTime.MAX));
            //每天的总订单数
            Integer orderCount = orderMapper.countByMap(map);
            orderCountList.add(orderCount == null ? 0 : orderCount);

            //每天的有效订单数（已完成）
            map.put("status", Orders.COMPLETED);
            Integer validOrderCount = orderMapper.countByMap(map);
            validOrderCountList.add(validOrderCount == null ? 0 : validOrderCount);
        }

        //时间区间内的订单总数
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).orElse(0);

        //时间区间内的有效订单数
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).orElse(0);

        //订单完成率
        Double orderCompletionRate = 0.0;
        if (totalOrderCount != 0) {
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
        }

        return OrderReportVO.builder()
                .dateList(dateList.stream().map(LocalDate::toString).collect(Collectors.joining(",")))
                .orderCountList(orderCountList.stream().map(String::valueOf).collect(Collectors.joining(",")))
                .validOrderCountList(validOrderCountList.stream().map(String::valueOf).collect(Collectors.joining(",")))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    /**
     * 统计指定时间区间内的销量排名前10
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDateTime begin, LocalDateTime end) {
        List<GoodsSalesDTO> salesTop10 = orderMapper.getSalesTop10(begin, end);

        String nameList = salesTop10.stream()
                .map(GoodsSalesDTO::getName)
                .collect(Collectors.joining(","));
        String numberList = salesTop10.stream()
                .map(x -> String.valueOf(x.getNumber()))
                .collect(Collectors.joining(","));

        return SalesTop10ReportVO.builder()
                .nameList(nameList)
                .numberList(numberList)
                .build();
    }

    /**
     * 导出近30天的运营数据报表
     *
     * @param response
     */
    @Override
    public void exportBusinessData(HttpServletResponse response) {
        //查询近30天的运营数据
        LocalDate dateBegin = LocalDate.now().minusDays(30);
        LocalDate dateEnd = LocalDate.now().minusDays(1);
        LocalDateTime beginTime = LocalDateTime.of(dateBegin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(dateEnd, LocalTime.MAX);

        //查询概览数据
        BusinessDataVO businessData = workspaceService.getBusinessData(beginTime, endTime);

        //通过POI将数据写入到Excel文件中
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ServletOutputStream outputStream = response.getOutputStream()) {

            //设置响应头，浏览器以附件方式下载
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment;filename=businessData.xlsx");

            XSSFSheet sheet = workbook.createSheet("运营数据报表");

            //标题样式
            CellStyle titleStyle = createStyle(workbook, (short) 16, true);
            //小标题样式
            CellStyle subTitleStyle = createStyle(workbook, (short) 12, true);
            //表头样式
            CellStyle headerStyle = createStyle(workbook, (short) 11, true);
            //正文样式
            CellStyle bodyStyle = createStyle(workbook, (short) 11, false);

            int rowIndex = 0;

            //1.报表标题
            Row titleRow = sheet.createRow(rowIndex++);
            titleRow.setHeightInPoints(28);
            XSSFCell titleCell = (XSSFCell) titleRow.createCell(0);
            titleCell.setCellValue("苍穹外卖运营数据报表");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));

            //2.统计时间区间
            Row timeRow = sheet.createRow(rowIndex++);
            XSSFCell timeCell = (XSSFCell) timeRow.createCell(0);
            timeCell.setCellValue("统计时间：" + dateBegin.format(DATE_FORMATTER) + " ~ " + dateEnd.format(DATE_FORMATTER));
            timeCell.setCellStyle(subTitleStyle);
            sheet.addMergedRegion(new CellRangeAddress(rowIndex - 1, rowIndex - 1, 0, 5));

            //3.概览数据
            Row overviewTitleRow = sheet.createRow(rowIndex++);
            XSSFCell overviewTitleCell = (XSSFCell) overviewTitleRow.createCell(0);
            overviewTitleCell.setCellValue("概览数据");
            overviewTitleCell.setCellStyle(subTitleStyle);
            sheet.addMergedRegion(new CellRangeAddress(rowIndex - 1, rowIndex - 1, 0, 5));

            String[] overviewHeaders = {"营业额", "有效订单数", "订单完成率", "平均客单价", "新增用户数"};
            Row overviewHeaderRow = sheet.createRow(rowIndex++);
            for (int i = 0; i < overviewHeaders.length; i++) {
                XSSFCell cell = (XSSFCell) overviewHeaderRow.createCell(i);
                cell.setCellValue(overviewHeaders[i]);
                cell.setCellStyle(headerStyle);
            }

            Row overviewValueRow = sheet.createRow(rowIndex++);
            String[] overviewValues = {
                    String.format("%.2f", businessData.getTurnover() == null ? 0.0 : businessData.getTurnover()),
                    String.valueOf(businessData.getValidOrderCount() == null ? 0 : businessData.getValidOrderCount()),
                    String.format("%.2f%%", (businessData.getOrderCompletionRate() == null ? 0.0 : businessData.getOrderCompletionRate()) * 100),
                    String.format("%.2f", businessData.getUnitPrice() == null ? 0.0 : businessData.getUnitPrice()),
                    String.valueOf(businessData.getNewUsers() == null ? 0 : businessData.getNewUsers())
            };
            for (int i = 0; i < overviewValues.length; i++) {
                XSSFCell cell = (XSSFCell) overviewValueRow.createCell(i);
                cell.setCellValue(overviewValues[i]);
                cell.setCellStyle(bodyStyle);
            }

            //空行
            rowIndex++;

            //4.明细数据
            Row detailTitleRow = sheet.createRow(rowIndex++);
            XSSFCell detailTitleCell = (XSSFCell) detailTitleRow.createCell(0);
            detailTitleCell.setCellValue("明细数据");
            detailTitleCell.setCellStyle(subTitleStyle);
            sheet.addMergedRegion(new CellRangeAddress(rowIndex - 1, rowIndex - 1, 0, 5));

            String[] detailHeaders = {"日期", "营业额", "有效订单数", "订单完成率", "平均客单价", "新增用户数"};
            Row detailHeaderRow = sheet.createRow(rowIndex++);
            for (int i = 0; i < detailHeaders.length; i++) {
                XSSFCell cell = (XSSFCell) detailHeaderRow.createCell(i);
                cell.setCellValue(detailHeaders[i]);
                cell.setCellStyle(headerStyle);
            }

            //逐天写入明细数据
            for (LocalDate date = dateBegin; !date.isAfter(dateEnd); date = date.plusDays(1)) {
                LocalDateTime begin = LocalDateTime.of(date, LocalTime.MIN);
                LocalDateTime end = LocalDateTime.of(date, LocalTime.MAX);
                BusinessDataVO dataVO = workspaceService.getBusinessData(begin, end);

                XSSFRow detailRow = (XSSFRow) sheet.createRow(rowIndex++);
                String[] values = {
                        date.format(DATE_FORMATTER),
                        String.format("%.2f", dataVO.getTurnover() == null ? 0.0 : dataVO.getTurnover()),
                        String.valueOf(dataVO.getValidOrderCount() == null ? 0 : dataVO.getValidOrderCount()),
                        String.format("%.2f%%", (dataVO.getOrderCompletionRate() == null ? 0.0 : dataVO.getOrderCompletionRate()) * 100),
                        String.format("%.2f", dataVO.getUnitPrice() == null ? 0.0 : dataVO.getUnitPrice()),
                        String.valueOf(dataVO.getNewUsers() == null ? 0 : dataVO.getNewUsers())
                };
                for (int i = 0; i < values.length; i++) {
                    XSSFCell cell = (XSSFCell) detailRow.createCell(i);
                    cell.setCellValue(values[i]);
                    cell.setCellStyle(bodyStyle);
                }
            }

            //设置列宽，保证可读性
            int[] columnWidths = {16, 14, 14, 14, 14, 14};
            for (int i = 0; i < columnWidths.length; i++) {
                sheet.setColumnWidth(i, columnWidths[i] * 256);
            }

            workbook.write(outputStream);
            outputStream.flush();
            log.info("导出运营数据报表成功");
        } catch (Exception e) {
            log.error("导出运营数据报表失败：{}", e.getMessage());
        }
    }

    /**
     * 构造 begin 到 end 之间（含首尾）的日期列表
     *
     * @param begin
     * @param end
     * @return
     */
    private List<LocalDate> buildDateList(LocalDateTime begin, LocalDateTime end) {
        List<LocalDate> dateList = new ArrayList<>();
        LocalDate current = begin.toLocalDate();
        LocalDate last = end.toLocalDate();
        while (!current.isAfter(last)) {
            dateList.add(current);
            current = current.plusDays(1);
        }
        return dateList;
    }

    /**
     * 创建统一风格的单元格样式
     *
     * @param workbook
     * @param fontSize
     * @param bold
     * @return
     */
    private CellStyle createStyle(XSSFWorkbook workbook, short fontSize, boolean bold) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("微软雅黑");
        font.setFontHeightInPoints(fontSize);
        font.setBold(bold);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
}
