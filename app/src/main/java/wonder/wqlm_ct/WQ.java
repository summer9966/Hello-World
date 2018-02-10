package wonder.wqlm_ct;

import android.content.Context;

/**
 * Created by feeling on 2018/2/10.
 */

public class WQ extends WQBase{

    // 当前高速模式支持的微信版本
    public final static String WeChatVersion_6_6_1 = "6.6.1";
    public final static String WeChatVersion_6_6_2 = "6.6.2";

    public WQ(Context context) {

    }

    public static void initWQ(Context context) {
        if (Tools.getWeChatVersion(context).equals(WeChatVersion_6_6_1)) {

        } else if (Tools.getWeChatVersion(context).equals(WeChatVersion_6_6_2)) {
            setWeChatVersion_6_6_2();
        }
    }

    private static void setWeChatVersion_6_6_2() {
        // 微信 聊天列表的联系人里面的消息内容 不可点击
        WID_CHAT_LIST_MESSAGE_TEXT = "com.tencent.mm:id/apt";
        // 微信 聊天列表的联系人 内括名字、消息内容、消息数字、头像 可点击
        WID_CHAT_LIST_DIALOG = "com.tencent.mm:id/app";
        // 微信 聊天列表的联系人里面的消息数字 不可点击
        WID_CHAT_LIST_MESSAGE_NUM = "com.tencent.mm:id/j4";
        // 微信 聊天列表的联系人里面的消息点（即屏蔽的群有消息） 不可点击
        WID_CHAT_LIST_MESSAGE_POT = "com.tencent.mm:id/apq";
        // 微信 聊天窗口 收到红包 可点击
        WID_CHAT_DIALOG_PACKET = "com.tencent.mm:id/ad8";
        // 微信 聊天窗口 收到红包 红包的文字（领取红包/查看红包） 不可点击
        WID_CHAT_DIALOG_PACKET_TEXT = "com.tencent.mm:id/ae_";
        // 微信 聊天窗口 收到红包 红包的文字内容 不可点击
        WID_CHAT_DIALOG_PACKET_CONTENT = "com.tencent.mm:id/ae9";
        // 微信 聊天窗口 打开红包弹窗 “開”
        WID_CHAT_PACKET_DIALOG_BUTTON = "com.tencent.mm:id/c4j";

        // 微信 自己发红包输入密码的界面
        WCN_PACKET_PAY = "com.tencent.mm.plugin.wallet_core.ui.m";
    }
}
