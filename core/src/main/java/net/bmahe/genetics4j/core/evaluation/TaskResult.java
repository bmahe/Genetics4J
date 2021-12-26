package net.bmahe.genetics4j.core.evaluation;

import java.util.List;

public record TaskResult<T> (int from, List<T> fitness) {
}