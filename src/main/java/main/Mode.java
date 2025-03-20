package main;

import java.math.BigInteger;

public enum Mode {
    Segment_Selector(16) {
        @Override
        int[] setData(BigInteger bigInteger, String[][] data) {
            BigInteger big;

            //

            data[0][0] = "描述符索引";
            big = bigInteger.shiftRight(3);
            data[2][0] = "索引值为0x" + big.toString(16).toUpperCase() + "\r\n指向" + big.toString(10) + "号描述符";

            data[0][13] = "描述符表\r\n指示器\r\n(TI)";
            data[2][13] = bigInteger.testBit(2) ? "描述符在LDT中" : "描述符在GDT中";

            data[0][14] = "请求特权级\r\n(RPL)";
            big = bigInteger;
            data[2][14] = "Ring " + big.and(new BigInteger("3", 16)).toString(10);

            //

            return new int[]{13, 1, 2};
        }
    }, Segment_Descriptor(64) {
        @Override
        int[] setData(BigInteger bigInteger, String[][] data) {
            BigInteger big = bigInteger;

            data[3][0] = "段存在位\r\n(P)";
            data[5][0] = bigInteger.testBit(47) ? "段存在于内存中" : "段尚未在内存中";

            data[3][1] = "目标特权级\r\n(DPL)";
            data[5][1] = "Ring " + big.and(new BigInteger("600000000000", 16)).shiftRight(45).toString(10).toUpperCase();

            if (bigInteger.testBit(44)) {
                data[3][3] = "描述符类型\r\n(S)";
                data[5][3] = "代码段/数据段";

                String s1_base_address = big.and(new BigInteger("FFFF0000", 16)).shiftRight(16).or(big.and(new BigInteger("FF00000000", 16)).shiftRight(16)).or(big.and(new BigInteger("FF00000000000000", 16)).shiftRight(32)).toString(16);
                s1_base_address = "0x" + "0".repeat(8 - s1_base_address.length()) + s1_base_address.toUpperCase() + "(31-0)";
                String s2 = big.and(new BigInteger("FFFF", 16)).or(big.shiftRight(32).and(new BigInteger("F0000", 16))).toString(16);
                s2 = "0x" + "0".repeat(5 - s2.length()) + s2.toUpperCase() + "(31-0)";

                //

                data[0][0] = "段基地址\r\n(31-24)";
                big = bigInteger.shiftRight(56);
                data[2][0] = "0x" + big.toString(16).toUpperCase() + "(31-24)\r\n" + s1_base_address;

                data[0][8] = "粒度\r\n(G)";
                data[2][8] = bigInteger.testBit(55) ? "4KByte" : "1Byte";

                data[0][9] = "默认操作尺寸\r\n(D/B)";
                data[2][9] = bigInteger.testBit(54) ? "32-bit" : "16-bit";

                data[0][10] = "64-bit代码段标志\r\n(L)";
                data[2][10] = bigInteger.testBit(53) ? "L" : "l";

                data[0][11] = "软件可以使用的位\r\n(AVL)";
                data[2][11] = bigInteger.testBit(52) ? "AVL" : "avl";

                data[0][12] = "段界限\r\n(19-16)";
                big = bigInteger.shiftRight(48).and(new BigInteger("F", 16));
                data[2][12] = "0x" + big.toString(16).toUpperCase() + "(19-16)\r\n" + s2;

                //

                data[3][4] = "描述符类别\r\n(TYPE)";
                data[5][4] = (bigInteger.testBit(43) ? ((bigInteger.testBit(42) ? "依从的" : "非依从的") + "代码段\r\n" + (bigInteger.testBit(41) ? "可执行,可读" : "只执行")) : ((bigInteger.testBit(42) ? "向下扩展的" : "向上扩展的") + "数据段\r\n" + (bigInteger.testBit(41) ? "可读,可写" : "只读"))) + "\r\n" + (bigInteger.testBit(40) ? "AVL" : "avl");

                data[3][8] = "段基地址\r\n(23-16)";
                big = bigInteger.shiftRight(32).and(new BigInteger("FF", 16));
                data[5][8] = "0x" + big.toString(16).toUpperCase() + "(23-16)\r\n" + s1_base_address;

                //

                data[6][0] = "段基地址\r\n(15-0)";
                big = bigInteger.shiftRight(16).and(new BigInteger("FFFF", 16));
                data[8][0] = "0x" + big.toString(16).toUpperCase() + "(15-0)\r\n" + s1_base_address;

                //

                data[9][0] = "段界限\r\n(15-0)";
                big = bigInteger.and(new BigInteger("FFFF", 16));
                data[11][0] = "0x" + big.toString(16).toUpperCase() + "(15-0)\r\n" + s2;

                //

                return new int[]{8, 1, 1, 1, 1, 4, 1, 2, 1, 4, 8, 16, 16};
            } else {
                data[3][3] = "描述符类型\r\n(S)";
                data[5][3] = "系统段";

                if (bigInteger.testBit(42)) {
                    if (bigInteger.testBit(43)) {
                        String s = big.and(new BigInteger("FFFF", 16)).or(big.shiftRight(32).and(new BigInteger("FFFF0000", 16))).toString(16);
                        s = "0x" + "0".repeat(8 - s.length()) + s.toUpperCase();

                        //

                        data[0][0] = "偏移地址\r\n(31-16)";
                        big = bigInteger.shiftRight(48);
                        data[2][0] = "0x" + big.toString(16).toUpperCase() + "(31-16)\r\n" + s;

                        //

                        data[3][4] = "描述符类别\r\n(TYPE)";
                        if (!bigInteger.testBit(41) && !bigInteger.testBit(40)) {
                            //00
                            data[5][4] = "调用门描述符";
                        } else if (!bigInteger.testBit(40)) {
                            //10
                            data[5][4] = "中断门描述符";
                        } else if (bigInteger.testBit(41)) {
                            //11
                            data[5][4] = "陷阱门描述符";
                        } else {
                            data[5][4] = "未知";
                        }

                        data[3][8] = "";
                        data[5][8] = "";

                        if (!bigInteger.testBit(41)) {
                            data[3][11] = "参数个数";
                            big = bigInteger.shiftRight(32).and(new BigInteger("1F", 16));
                            data[5][11] = big.toString(10).toUpperCase();
                        } else {
                            data[3][11] = "";
                            data[5][11] = "";
                        }

                        //

                        data[6][0] = "段选择子";
                        big = bigInteger.shiftRight(16).and(new BigInteger("FFFF", 16));
                        data[8][0] = big.shiftRight(3).toString(10).toUpperCase() + "号描述符\r\n" + (big.testBit(2) ? "描述符在LDT中" : "描述符在GDT中") + "\r\nRing " + big.and(new BigInteger("30000", 16)).shiftRight(16).toString(10);

                        //

                        data[9][0] = "偏移地址\r\n(15-0)";
                        big = bigInteger.and(new BigInteger("FFFF", 16));
                        data[11][0] = "0x" + big.toString(16).toUpperCase() + "(15-0)\r\n" + s + "(19-0)";

                        //

                        return new int[]{16, 1, 2, 1, 4, 3, 5, 16, 16};
                    } else {

                        //

                        data[0][0] = "";
                        data[2][0] = "";

                        //

                        data[3][4] = "描述符类别\r\n(TYPE)";
                        if (!bigInteger.testBit(41) && bigInteger.testBit(40)) {
                            //01
                            data[5][4] = "任务门描述符";
                        } else {
                            data[5][4] = "未知";
                        }

                        data[3][8] = "";
                        data[5][8] = "";

                        //

                        data[6][0] = "段选择子";
                        big = bigInteger.shiftRight(16).and(new BigInteger("FFFF", 16));
                        data[8][0] = big.shiftRight(3).toString(10).toUpperCase() + "号描述符\r\n" + (big.testBit(2) ? "描述符在LDT中" : "描述符在GDT中") + "\r\nRing " + big.and(new BigInteger("30000", 16)).shiftRight(16).toString(10);

                        //

                        data[9][0] = "";
                        data[11][0] = "";

                        //

                        return new int[]{16, 1, 2, 1, 4, 8, 16, 16};
                    }
                } else {
                    String s1_base_address = big.and(new BigInteger("FFFF0000", 16)).shiftRight(16).or(big.and(new BigInteger("FF00000000", 16)).shiftRight(16)).or(big.and(new BigInteger("FF00000000000000", 16)).shiftRight(32)).toString(16);
                    s1_base_address = "0x" + "0".repeat(8 - s1_base_address.length()) + s1_base_address.toUpperCase() + "(31-0)";
                    String s2 = big.and(new BigInteger("FFFF", 16)).or(big.shiftRight(32).and(new BigInteger("F0000", 16))).toString(16);
                    s2 = "0x" + "0".repeat(5 - s2.length()) + s2.toUpperCase() + "(31-0)";

                    //

                    data[0][0] = "段基地址\r\n(31-24)";
                    big = bigInteger.shiftRight(56);
                    data[2][0] = "0x" + big.toString(16).toUpperCase() + "(31-24)\r\n" + s1_base_address;

                    data[0][8] = "粒度\r\n(G)";
                    data[2][8] = bigInteger.testBit(55) ? "4KByte" : "1Byte";

                    data[0][9] = "";
                    data[2][9] = "";

                    data[0][10] = "";
                    data[2][10] = "";

                    data[0][11] = "软件可以使用的位\r\n(AVL)";
                    data[2][11] = bigInteger.testBit(52) ? "AVL" : "avl";

                    data[0][12] = "段界限\r\n(19-16)";
                    big = bigInteger.shiftRight(48).and(new BigInteger("F", 16));
                    data[2][12] = "0x" + big.toString(16).toUpperCase() + "(19-16)\r\n" + s2;

                    //

                    data[3][4] = "描述符类别\r\n(TYPE)";
                    if (bigInteger.testBit(40) && bigInteger.testBit(43)) {
                        data[5][4] = "TSS段描述符\r\n" + (bigInteger.testBit(41) ? "B" : "b");
                    } else if (!bigInteger.testBit(40) && bigInteger.testBit(41) && !bigInteger.testBit(43)) {
                        data[5][4] = "LDT段描述符";
                    } else {
                        data[5][4] = "未知";
                    }

                    data[3][8] = "段基地址\r\n(23-16)";
                    big = bigInteger.shiftRight(32).and(new BigInteger("FF", 16));
                    data[5][8] = "0x" + big.toString(16).toUpperCase() + "(23-16)\r\n" + s1_base_address;

                    //

                    data[6][0] = "段基地址\r\n(15-0)";
                    big = bigInteger.shiftRight(16).and(new BigInteger("FFFF", 16));
                    data[8][0] = "0x" + big.toString(16).toUpperCase() + "(15-0)\r\n" + s1_base_address;

                    //

                    data[9][0] = "段界限\r\n(15-0)";
                    big = bigInteger.and(new BigInteger("FFFF", 16));
                    data[11][0] = "0x" + big.toString(16).toUpperCase() + "(15-0)\r\n" + s2;

                    //

                    return new int[]{8, 1, 1, 1, 1, 4, 1, 2, 1, 4, 8, 16, 16};
                }
            }
        }
    }, PDE_PTE(32) {
        @Override
        int[] setData(BigInteger bigInteger, String[][] data) {
            String s = bigInteger.shiftRight(12).toString(16);
            s="0x" + "0".repeat(5-s.length()) + s + "(19-0)";

            //

            data[0][0] = "页物理基地址\r\n(19-4)";
            data[2][0] = "0x" + bigInteger.shiftRight(16).toString(16) + "(19-4)\r\n" + s;

            //

            data[3][0] = "页物理基地址\r\n(3-0)";
            data[5][0] = "0x" + bigInteger.shiftRight(16).toString(16) + "(3-0)\r\n" + s;

            data[3][4] = "软件可以使用的位\r\n(AVL)";
            data[5][4] = bigInteger.and(new BigInteger("e00",16)).shiftRight(9).toString(16);

            data[3][7] = "全局位\r\n(G)";
            data[5][7] = bigInteger.testBit(8)?"G":"g";

            data[3][8] = "页属性表支持位\r\n(PAT)\r\n页目录项(PDE)此位无效";
            data[5][8] = bigInteger.testBit(7)?"PAT":"pat";

            data[3][9] = "脏位\r\n(D)";
            data[5][9] = bigInteger.testBit(6)?"此项所指向的页已被写过数据":"此项所指向的页未被写过数据";

            data[3][10] = "访问位\r\n(A)";
            data[5][10] = bigInteger.testBit(5)?"此项所指向的页已被读过数据":"此项所指向的页未被读过数据";

            data[3][11] = "页级高速缓存禁止位\r\n(PCD)";
            data[5][11] = bigInteger.testBit(4)?"PCD":"pcd";

            data[3][12] = "页级通写位\r\n(PWT)";
            data[5][12] = bigInteger.testBit(3)?"PWT":"pwt";

            data[3][13] = "用户/管理位\r\n(US)";
            data[5][13] = bigInteger.testBit(2)?"允许所有特权级别的程序访问":"只允许特权级为0/1/2的程序访问";

            data[3][14] = "页级高速缓存禁止位\r\n(RW)";
            data[5][14] = bigInteger.testBit(1)?"可读可写":"只读";

            data[3][15] = "页级高速缓存禁止位\r\n(P)";
            data[5][15] = bigInteger.testBit(0)?"页存在于内存中":"页尚未在内存中";

            //

            return new int[]{16, 4, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1};
        }
    };
    final int bits_length;

    Mode(int bits_length) {
        this.bits_length = bits_length;
    }

    abstract int[] setData(BigInteger bigInteger, String[][] data);
}
