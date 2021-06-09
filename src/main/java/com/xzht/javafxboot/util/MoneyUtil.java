package com.xzht.javafxboot.util;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/**
 * @author Erting.Wang
 * @desciption 类说明
 * @date 2021/6/9 2:04 下午
 */
@Slf4j
public class MoneyUtil {

    public static void main(String[] args) {
        String toNumber = ChineseConvertToNumber("柒仟壹佰玖拾伍圆整");
        log.debug("toNumber={}", toNumber);
    }

    public static String toRmbString(BigDecimal decimal) {
        String yuan = "亿千百拾万千百拾元角分";
        String digit = "零壹贰叁肆伍陆柒捌玖";
        String result = "";
        int y = (int) Math.round(decimal.doubleValue() * 100 - 0.5);

        int dec = y % 100;
        y = y / 100;
        String money = String.valueOf(y);
        if (y == 0) {
            return result + "零元";
        }

        if (dec == 0) {
            result = "整" + result;
        } else {
            int a = dec / 10;
            int b = dec % 10;
            if (a != 0) {
                result = result + digit.charAt(a) + "角";
            }
            if (b != 0) {
                result = result + digit.charAt(b) + "分";
            }
        }
        if (y == 10) {
            result = "拾元" + result;
            return result;
        } else {
            int j = money.length() - 1;
            int k = 8;
            while (j >= 0) {
                if (money.charAt(j) == '0') {
                    j--;
                    k--;
                }
                result = digit.charAt(money.charAt(j) - '0') + "" + yuan.charAt(k) + "" + result;
                j--;
                k--;
            }
            return result;
        }
    }

    /**
     * 大写金额转数字
     */
    public static String ChineseConvertToNumber(String chineseAmount) {
        if (chineseAmount == null || chineseAmount.length() <= 0 || chineseAmount == "") {
            return null;
        }
        //移除计算干扰文字
        chineseAmount = chineseAmount
                .replace("元", "")
                .replace("整", "");

        // 字符切割
        char[] wordCharArray = chineseAmount.toCharArray();

        //最终要返回的数字金额
        BigDecimal numberAmount = BigDecimal.ZERO;

        //金额位标志量
        //表示有分位
        boolean fen = false;
        //表示有角位
        boolean jiao = false;
        // 表示个位不为0
        boolean yuan = false;
        //表示有十位
        boolean shi = false;
        //表示有百位
        boolean bai = false;
        //表示有千位
        boolean qian = false;
        //表示有万位
        boolean wan = false;
        //表示有亿位
        boolean yi = false;

        //从低位到高位计算
        for (int i = (wordCharArray.length - 1); i >= 0; i--) {
            //当前位金额值
            BigDecimal currentPlaceAmount = BigDecimal.ZERO;

            //判断当前位对应金额标志量
            if (wordCharArray[i] == '分') {
                fen = true;
                continue;
            } else if (wordCharArray[i] == '角') {
                jiao = true;
                continue;
            } else if (wordCharArray[i] == '元') {
                yuan = true;
                continue;
            } else if (wordCharArray[i] == '拾') {
                shi = true;
                continue;
            } else if (wordCharArray[i] == '佰') {
                bai = true;
                continue;
            } else if (wordCharArray[i] == '仟') {
                qian = true;
                continue;
            } else if (wordCharArray[i] == '万') {
                wan = true;
                continue;
            } else if (wordCharArray[i] == '亿') {
                yi = true;
                continue;
            }

            //根据标志量转换金额为实际金额
            double t = 0;
            if (fen) {
                t = ConvertNameToSmall(wordCharArray[i]) * 0.01;
            } else if (jiao) {
                t = ConvertNameToSmall(wordCharArray[i]) * 0.1;
            } else if (shi) {
                t = ConvertNameToSmall(wordCharArray[i]) * 10;
            } else if (bai) {
                t = ConvertNameToSmall(wordCharArray[i]) * 100;
            } else if (qian) {
                t = ConvertNameToSmall(wordCharArray[i]) * 1000;
            } else {
                t = ConvertNameToSmall(wordCharArray[i]);
            }
            currentPlaceAmount = new BigDecimal(t);
            //每万位处理
            if (yi) {
                currentPlaceAmount = currentPlaceAmount.multiply(new BigDecimal(100000000));
            } else if (wan) {
                currentPlaceAmount = currentPlaceAmount.multiply(new BigDecimal(10000));
            }
            numberAmount = numberAmount.add(currentPlaceAmount);
            // 重置状态
            //yi = false; // 亿和万  不可重置 下次循环还会用到
            //wan = false;
            fen = false;
            jiao = false;
            shi = false;
            bai = false;
            qian = false;
            yuan = false;
        }
        return numberAmount.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    }

    /**
     * 转换中文数字为阿拉伯数字
     *
     * @param chinese
     * @return
     * @throws Exception
     */
    private static int ConvertNameToSmall(char chinese) {
        int number = 0;
        String s = String.valueOf(chinese);
        switch (s) {
            case "零":
                number = 0;
                break;
            case "壹":
                number = 1;
                break;
            case "贰":
                number = 2;
                break;
            case "叁":
                number = 3;
                break;
            case "肆":
                number = 4;
                break;
            case "伍":
                number = 5;
                break;
            case "陆":
                number = 6;
                break;
            case "柒":
                number = 7;
                break;
            case "捌":
                number = 8;
                break;
            case "玖":
                number = 9;
                break;
        }
        return number;
    }
}
