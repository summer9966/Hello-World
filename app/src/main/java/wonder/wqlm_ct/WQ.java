package wonder.wqlm_ct;

/**
 * Created by feeling on 2018/1/20.
 */

public class WQ {


    // 本程序包名
    public final static String SELF_PACKAGE_NAME = "wonder.wqlm_ct";
    // 微信包名
    public final static String WECHAT_PACKAGE_NAME = "com.tencent.mm";


    /*
        Class Name
    */
    // 通知监听类名
    public final static String SELFCN_NOTIFICATION = "WQNotificationService";
    // 辅助服务类名
    public final static String SELFCN_ACCESSBILITY = "WQAccessibilityService";

    // 微信 聊天列表、聊天窗口
    public final static String WCN_LAUNCHER = "com.tencent.mm.ui.LauncherUI";
    // 微信 红包“開”的窗口
    public final static String WCN_PACKET_RECEIVE = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI";
    // 微信 自己发红包的窗口
    public final static String WCN_PACKET_SEND = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyPrepareUI";
    // 微信 自己发红包输入密码的界面
    public final static String WCN_PACKET_PAY = "com.tencent.mm.plugin.wallet_core.ui.l";
    // 微信 红包详情
    public final static String WCN_PACKET_DETAIL = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI";



    /*
        Resource-id
    */
    // 微信 聊天列表的联系人列表 ListView
    public final static String WID_CHAT_LIST_LISTVIEW = "com.tencent.mm:id/c3p";
    //
    //
    // 目前，兼容模式也要同步更新的地方  -------- 开始
    //
    // 微信 聊天列表的联系人里面的消息内容 不可点击
    public final static String WID_CHAT_LIST_MESSAGE_TEXT = "com.tencent.mm:id/apv";
    // 微信 聊天列表的联系人 内括名字、消息内容、消息数字、头像  可点击
    public final static String WID_CHAT_LIST_DIALOG = "com.tencent.mm:id/apr";
    // 微信 聊天列表的联系人里面的消息数字 不可点击
    public final static String WID_CHAT_LIST_MESSAGE_NUM = "com.tencent.mm:id/iu";
    // 微信 聊天列表的联系人里面的消息点（即屏蔽的群有消息） 不可点击
    public final static String WID_CHAT_LIST_MESSAGE_POT = "com.tencent.mm:id/aps";
    // 微信 红包详情的后退按钮
    public final static String WID_PACKET_DETAIL_BACK_BUTTON = "com.tencent.mm:id/ho";
    //
    //
    // 目前，兼容模式也要同步更新的地方  -------- 结束
    //


    // 微信 聊天窗口 ListView
    public final static String WID_CHAT_DIALOG_LISTVIEW = "com.tencent.mm:id/a_h";
    // 微信 聊天窗口 收到红包 可点击
    public final static String WID_CHAT_DIALOG_PACKET = "com.tencent.mm:id/ada";
    // 微信 聊天窗口 收到红包 红包的文字 不可点击
    public final static String WID_CHAT_DIALOG_PACKET_TEXT = "com.tencent.mm:id/aeb";
    // 微信 聊天窗口 收到红包 XX领取了XX的红包
    public final static String WID_CHAT_DIALOG_HAD_OPEN_PACKET = "com.tencent.mm:id/j_";

    // 微信 聊天窗口 打开红包弹窗  “開”
    public final static String WID_CHAT_PACKET_DIALOG_BUTTON = "com.tencent.mm:id/c2i";


    /*
        Resource text
    */
    public final static String WT_PACKET = "[微信红包]";
    public final static String WT_GET_PACKET = "领取红包";
    public final static String WT_SEE_PACKET = "查看红包";
    public final static String WT_GET_PACKET_SELF = "你领取了凡人的红包";






    public final static int W_otherWindow = 0;
    public final static int W_chatListWindow = 1;
    public final static int W_chatWindow = 2;
    public final static int W_packetWindow = 3;
    public final static int W_packetDetailWindow = 4;
    public final static int W_sendPacketWindow = 5;
    public final static int W_packetPayWindow = 6;
    public static int currentWindow = W_otherWindow;

    public final static int W_otherStatus = 0;
    public final static int W_openedPacketSendStatus = 1;
    public final static int W_openedPayStatus = 2;
    public final static int W_intoChatDialogStatus = 3;
    public final static int W_gotSelfPacketStatus = 4;
    public static int currentSelfPacketStatus = W_otherStatus;

    public static boolean isPreviouslyLockScreen = false;

    public static boolean isGotNotification = false;
    public static boolean isClickedNewMessageList = false;
    public static boolean isGotPacket = false;
    public static boolean needBacktoMessageList = false;

    public static void setCurrentSelfPacketStatus(int status) {
        currentSelfPacketStatus = status;
    }
}
