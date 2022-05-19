package com.example.byteplus_effects_plugin.core.avatar;

import com.bytedance.labcv.effectsdk.BytedEffectConstants;

/**
 * Created on 5/8/21 11:55 AM
 */
public interface AvatarInterface {
    /** {zh} 
     * @brief 初始化 SDK
     */
    /** {en} 
     * @brief Initialization SDK
     */

    int init();

    /** {zh} 
     * @brief SDK 处理
     * @param texture 纹理
     * @param width 宽
     * @param height 高
     * @param frontCamera 是否为前置摄像头
     * @param sensorRotation 设备角度
     * @param timestamp 时间戳
     * @return 输出纹理
     */
    /** {en} 
     * @brief SDK  processing
     * @param texture  texture
     * @param width  width
     * @param height  height
     * @param frontCamera  whether it is a front camera
     * @param sensorRotation  device angle
     * @param timestamp  timestamp
     * @return  output texture
     */

    int process(int texture, int width, int height, boolean frontCamera,
                BytedEffectConstants.Rotation sensorRotation, long timestamp);

    /** {zh} 
     * @brief 销毁 SDK
     */
    /** {en} 
     * @brief Destroy SDK
     */

    int destroy();

    /** {zh} 
     * @brief 设置 avatar 路径
     * @param avatarPath avatar 素材绝对路径
     */
    /** {en} 
     * @brief Set avatar path
     * @param avatarPath avatar material absolute path
     */

    void setAvatar(String avatarPath);

    /** {zh} 
     * @brief 发送消息
     * @param msg 消息
     */
    /** {en} 
     * @brief Send message
     * @param msg  message
     */

    void sendMessage(EffectMsg msg);

    /** {zh} 
     * @brief Avatar 发送消息
     */
    /** {en} 
     * @brief Avatar  Send message
     */

    public static class EffectMsg {
        public EffectMsg(int msgId, long arg1, long arg2, String arg3) {
            this.msgId = msgId;
            this.arg1 = arg1;
            this.arg2 = arg2;
            this.arg3 = arg3;
        }

        public int msgId;
        public long arg1;
        public long arg2;
        public String arg3;
    }
}
