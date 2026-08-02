import type { PageData } from '@/types/api';

export const createEmptyPageData = <T>(): PageData<T> => ({
  content: [] as T[],
  totalElements: 0,
  totalPages: 0,
  number: 0,
  size: 0,
  first: true,
  last: true,
  numberOfElements: 0,
  empty: true
});

export const normalizePageData = <T>(page: Partial<PageData<T>> | null | undefined): PageData<T> => {
  if (!page || typeof page !== 'object') {
    return createEmptyPageData<T>();
  }

  const content = Array.isArray(page.content) ? page.content : [];

  return {
    content,
    totalElements: typeof page.totalElements === 'number' ? page.totalElements : 0,
    totalPages: typeof page.totalPages === 'number' ? page.totalPages : 0,
    number: typeof page.number === 'number' ? page.number : 0,
    size: typeof page.size === 'number' ? page.size : content.length,
    first: page.first === true,
    last: page.last === true,
    numberOfElements: typeof page.numberOfElements === 'number' ? page.numberOfElements : content.length,
    empty: page.empty === true || content.length === 0
  };
};
