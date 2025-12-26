package com.edward.order.utils;

import com.edward.order.repository.SlugRepository;

import java.text.Normalizer;

public final class SlugUtils {

    private String toSlug(String input) {
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

    public static <R extends SlugRepository> String generateUniqueSlug(String input, R repository) {

        String baseSlug = new SlugUtils().toSlug(input);
        String slug = baseSlug;
        int i = 1;

        while (repository.existsBySlug(slug)) {
            slug = baseSlug + "-" + i;
            i++;
        }

        return slug;
    }
}
