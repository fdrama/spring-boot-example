package com.example.controller.resp;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.StringJoiner;

/**
 * @author fdrama
 * @date 2022年08月19日 13:41
 */
@Getter
@Setter
public class PageResult<E> extends BaseResult<List<E>> {

    private static final long serialVersionUID = 888757687538208808L;
    private Long total;

    private Integer currentPage;

    private Integer pageSize;

    private Boolean fetchAll;

    private List<E> data;

    private int code;

    private String msg;


    private PageResult(Builder<E> builder) {
        this.data = builder.data;
        this.total = builder.total;
        this.currentPage = builder.currentPage;
        this.pageSize = builder.pageSize;
        this.fetchAll = builder.fetchAll;
        this.code = builder.code;
        this.msg = builder.msg;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PageResult.class.getSimpleName() + "[", "]")
                .add("total=" + total)
                .add("currentPage=" + currentPage)
                .add("pageSize=" + pageSize)
                .add("fetchAll=" + fetchAll)
                .add("data=" + data)
                .add("code='" + code + "'")
                .add("msg='" + msg + "'")
                .toString();
    }


    public static class Builder<E> {

        private Long total;

        private Integer currentPage;

        private Integer pageSize;

        private Boolean fetchAll;

        private int code;

        private String msg;

        private List<E> data;


        public Builder(int currentPage, int pageSize) {
            this.pageSize = pageSize;
            this.currentPage = currentPage;
        }

        public Builder() {

        }


        public Builder<E> total(long total) {
            this.total = total;
            return this;
        }

        public Builder<E> currentPage(int currentPage) {
            this.currentPage = currentPage;
            return this;
        }

        public Builder<E> pageSize(int pageSize) {
            this.pageSize = pageSize;
            return this;
        }


        public Builder<E> fetchAll(boolean fetchAll) {
            this.fetchAll = fetchAll;
            return this;
        }

        public Builder<E> data(List<E> data) {
            this.data = data;
            return this;
        }

        public PageResult<E> buildForFailed(GlobalError globalError) {
            this.code = globalError.getErrorCode();
            this.msg = globalError.getErrorMsg();
            return new PageResult<E>(this);
        }

        public PageResult<E> buildForFailed(int code, String msg) {
            this.code = code;
            this.msg = msg;
            return new PageResult<E>(this);
        }

        public PageResult<E> buildForSuccess(List<E> data) {
            this.data = data;
            this.code = ResultCodeEnum.SUCCESS.getErrorCode();
            this.msg = ResultCodeEnum.SUCCESS.getErrorMsg();
            return new PageResult<E>(this);
        }

        public PageResult<E> buildForSuccess() {
            return new PageResult<E>(this);
        }
    }

}
