package wonder.wqlm_ct;

/**
 * Created by feeling on 2018/1/20.
 */

public class WQBase {


    // 本程序包名
    public static String SELF_PACKAGE_NAME = "wonder.wqlm_ct";
    // 微信包名
    public static String WECHAT_PACKAGE_NAME = "com.tencent.mm";
    // 微信
    public static String WECHAT = "微信";


    /*
        Class Name
    */
    // 通知监听类名
    public static String SELFCN_NOTIFICATION = "WQNotificationService";
    // 辅助服务类名
    public static String SELFCN_ACCESSBILITY = "WQAccessibilityService";

    // 微信 聊天列表、聊天窗口
    public static String WCN_LAUNCHER = "com.tencent.mm.ui.LauncherUI";
    // 微信 红包“開”的窗口
    public static String WCN_PACKET_RECEIVE = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI";
    // 微信 自己发红包的窗口
    public static String WCN_PACKET_SEND = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyPrepareUI";
    // 微信 自己发红包输入密码的界面
    public static String WCN_PACKET_PAY = "com.tencent.mm.plugin.wallet_core.ui.l";
    // 微信 红包详情
    public static String WCN_PACKET_DETAIL = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI";

    // 微信 红包详情
    public static String WCN_PACKET_BUTTON = "android.widget.Button";



    /*
        Resource-id
    */
    // 微信 聊天列表的联系人列表 ListView
    public static String WID_CHAT_LIST_LISTVIEW = "com.tencent.mm:id/c3p";
    // 微信 聊天列表的联系人里面的消息内容 不可点击
    public static String WID_CHAT_LIST_MESSAGE_TEXT = "com.tencent.mm:id/apv";
    // 微信 聊天列表的联系人 内括名字、消息内容、消息数字、头像  可点击
    public static String WID_CHAT_LIST_DIALOG = "com.tencent.mm:id/apr";
    // 微信 聊天列表的联系人里面的消息数字 不可点击
    public static String WID_CHAT_LIST_MESSAGE_NUM = "com.tencent.mm:id/iu";
    // 微信 聊天列表的联系人里面的消息点（即屏蔽的群有消息） 不可点击
    public static String WID_CHAT_LIST_MESSAGE_POT = "com.tencent.mm:id/aps";
    // 微信 红包详情的后退按钮
    public static String WID_PACKET_DETAIL_BACK_BUTTON = "com.tencent.mm:id/ho";


    // 微信 聊天窗口 ListView
    public static String WID_CHAT_DIALOG_LISTVIEW = "com.tencent.mm:id/a_h";
    // 微信 聊天窗口 收到红包 可点击
    public static String WID_CHAT_DIALOG_PACKET = "com.tencent.mm:id/ada";
    // 微信 聊天窗口 收到红包 红包的文字（领取红包/查看红包） 不可点击
    public static String WID_CHAT_DIALOG_PACKET_TEXT = "com.tencent.mm:id/aeb";
    // 微信 聊天窗口 收到红包 红包的文字内容 不可点击
    public static String WID_CHAT_DIALOG_PACKET_CONTENT = "com.tencent.mm:id/aea";
    // 微信 聊天窗口 收到红包 XX领取了XX的红包
    public static String WID_CHAT_DIALOG_HAD_OPEN_PACKET = "com.tencent.mm:id/j_";

    // 微信 聊天窗口 打开红包弹窗  “開”
    public static String WID_CHAT_PACKET_DIALOG_BUTTON = "com.tencent.mm:id/c2i";


    /*
        Resource text
    */
    public static String WT_PACKET = "[微信红包]";
    public static String WT_GET_PACKET = "领取红包";
    public static String WT_SEE_PACKET = "查看红包";
    public static String WT_GET_PACKET_SELF = "你领取了凡人的红包";
    public static String WT_OPEN_SEND_A_PACKET = "发了一个红包";


    public static int W_otherStatus = 0;
    public static int W_openedPacketSendStatus = 1;
    public static int W_openedPayStatus = 2;
    public static int W_intoChatDialogStatus = 3;
    public static int W_gotSelfPacketStatus = 4;
    public static int currentSelfPacketStatus = W_otherStatus;

    public static boolean isPreviouslyLockScreen = false;

    public static boolean isGotNotification = false;
    public static boolean isClickedNewMessageList = false;
    public static boolean isGotPacket = false;

    public static int backtoMessageListOther = 0;
    public static int backtoMessageListReceiveUI = 1;
    public static int backtoMessageListChatDialog = 2;
    public static int backtoMessageListStatus = backtoMessageListOther;

    public static void setCurrentSelfPacketStatus(int status) {
        currentSelfPacketStatus = status;
    }
}
