/*
 * This file is part of Politics.
 *
 * Copyright (c) 2019 Oliver Stanley
 * Politics is licensed under the Affero General Public License Version 3.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pw.ollie.politics.util.collect;

import java.util.List;

/**
 * Represents a paged {@link List}.
 *
 * @param <E> the type of the elements
 */
public interface PagedList<E> extends List<E> {
    /**
     * Gets the sub-{@link List} of this PagedList which is comprised of the elements on the given page.
     *
     * @param page the page number of the page to get
     * @return the given page
     */
    List<E> getPage(int page);

    /**
     * Gets the number of pages this PagedList has with the current elements per page setting.
     *
     * @return this list's current number of pages
     */
    int pages();

    /**
     * Gets the currently set number of elements per page.
     *
     * @return the elements per page for this PagedList
     */
    int getElementsPerPage();

    /**
     * Gets whether the pages of this PagedList will automatically update when changes are made to the List. When many
     * changes are about to be made at once it is recommended to disable this until the end, then call
     * {@link #recalculatePages()}.
     *
     * @return whether this PagedList automatically updates pages when changes are made
     */
    boolean isAutoRefresh();

    /**
     * Gets whether this PagedList will refresh pages when accessed. Usually recommended to be disabled.
     *
     * @return whether pages are refreshed whenever the PagedList is accessed
     */
    boolean isRefreshOnGet();

    /**
     * Sets the number of elements per page.
     *
     * @param elementsPerPage the new number of elements per page
     */
    void setElementsPerPage(int elementsPerPage);

    /**
     * Sets whether the PagedList should refresh pages whenever written to.
     *
     * @param autoRefresh whether to refresh pages on write
     */
    void setAutoRefresh(boolean autoRefresh);

    /**
     * Sets whether the PagedList should refresh pages whenever read.
     *
     * @param refreshOnGet whether to refresh pages on read
     */
    void setRefreshOnGet(boolean refreshOnGet);

    /**
     * Force refresh the pages in this PagedList.
     */
    void recalculatePages();
}
