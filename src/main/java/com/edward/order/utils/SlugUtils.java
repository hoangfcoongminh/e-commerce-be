package com.edward.order.utils;

import java.text.Normalizer;

public final class SlugUtils {

    public static String toSlug(String input) {
        //Chuẩn hóa Unicode - Bỏ dấu
        String noAccent = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        //ower case
        String lower = noAccent.toLowerCase();

        //Loại bỏ ký tự không hợp lệ
        String cleaned = lower.replaceAll("[^a-z0-9\\s-]", "");

        //Replace khoảng trắng bằng gạch ngang
        String hyphen = cleaned.trim().replaceAll("\\s+", "-");

        //Xóa dấu gạch "-" dư
        return hyphen.replaceAll("-{2,}", "-");
    }
}
