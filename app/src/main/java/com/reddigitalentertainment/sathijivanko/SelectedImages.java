package com.reddigitalentertainment.sathijivanko;

import android.net.Uri;

import java.util.List;

public class SelectedImages {
    private static List<Uri> list = null;

    public static List<Uri> getList() {
        return list;
    }

    public static void setList(List<Uri> list) {
        SelectedImages.list = list;
    }
}
