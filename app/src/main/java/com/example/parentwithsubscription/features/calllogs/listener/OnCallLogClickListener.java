package com.example.parentwithsubscription.features.calllogs.listener;

import com.example.parentwithsubscription.features.calllogs.model.CallLogs;

// OnContactClickListener.java
public interface OnCallLogClickListener {
    void onContactClick(CallLogs contact);

    void onContactBlock(CallLogs contact);
    void onContactUnblock(CallLogs contact);
}
