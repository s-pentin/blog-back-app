package org.blog.models.request;

import java.util.Set;

public record PostRequest(String title, String text, Set<String> tags) {
}