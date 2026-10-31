package it.ddot.core.kbase.io;

import it.ddot.core.hostreader.asciidoc.Adoc2Kg;
import it.ddot.core.io.ISourceHandler;
import it.ddot.core.kbase.IKnowledgeItemResource;
import it.ddot.core.kbase.inmem.Kgraph;
import it.ddot.core.kbase.inmem.Knode;
import org.jspecify.annotations.Nullable;

/**
 * An {@link ISourceHandler} that is backed by a {@link Kgraph} and exposes it, so readers can add graph data
 * that does not fit the (subject, predicate, object) triple-event stream — e.g. node labels, types and inline
 * HTML resources discovered by the AsciiDoc reader.
 *
 * <p>It lives here, not in {@code ddotit-core}, because {@link Kgraph} is built on graphinout's JSON types and
 * {@code ddotit-core} depends on nothing but jspecify. The AsciiDoc reader in core knows no {@code Kgraph}: it hands
 * the nodes it finds to any handler that is also an {@link Adoc2Kg.IHandler}, which this interface is, creating them
 * in {@link #kgraph()}.
 */
public interface IKgraphSourceHandler extends ISourceHandler, Adoc2Kg.IHandler {

    /** Expose the active kgraph for advanced data */
    Kgraph kgraph();

    /** An AsciiDoc document or section the AsciiDoc reader found: a node with its label, inline HTML and type. */
    @Override
    default void createNode(String nodeId, @Nullable String label, @Nullable String inlineHtmlContent,
                            @Nullable String nodeType) {
        Knode knode = kgraph().getOrCreateNodeWithIdAndLabel(nodeId);
        if (label != null) knode.addLabelWithoutLanguage(label);
        if (inlineHtmlContent != null) knode.resource(IKnowledgeItemResource.inlineText_Html(inlineHtmlContent));
        if (nodeType != null) kgraph().addTypeName(knode, nodeType);
    }

}
