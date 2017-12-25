package wowjoy.fruits.ms.module.logs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FruitLogsExample {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table fruits_logs
     *
     * @mbg.generated Thu Dec 21 19:20:22 CST 2017
     */
    protected String orderByClause;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table fruits_logs
     *
     * @mbg.generated Thu Dec 21 19:20:22 CST 2017
     */
    protected boolean distinct;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table fruits_logs
     *
     * @mbg.generated Thu Dec 21 19:20:22 CST 2017
     */
    protected List<Criteria> oredCriteria;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_logs
     *
     * @mbg.generated Thu Dec 21 19:20:22 CST 2017
     */
    public FruitLogsExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_logs
     *
     * @mbg.generated Thu Dec 21 19:20:22 CST 2017
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_logs
     *
     * @mbg.generated Thu Dec 21 19:20:22 CST 2017
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_logs
     *
     * @mbg.generated Thu Dec 21 19:20:22 CST 2017
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_logs
     *
     * @mbg.generated Thu Dec 21 19:20:22 CST 2017
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_logs
     *
     * @mbg.generated Thu Dec 21 19:20:22 CST 2017
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_logs
     *
     * @mbg.generated Thu Dec 21 19:20:22 CST 2017
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_logs
     *
     * @mbg.generated Thu Dec 21 19:20:22 CST 2017
     */
    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_logs
     *
     * @mbg.generated Thu Dec 21 19:20:22 CST 2017
     */
    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_logs
     *
     * @mbg.generated Thu Dec 21 19:20:22 CST 2017
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_logs
     *
     * @mbg.generated Thu Dec 21 19:20:22 CST 2017
     */
    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table fruits_logs
     *
     * @mbg.generated Thu Dec 21 19:20:22 CST 2017
     */
    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andUuidIsNull() {
            addCriterion("uuid is null");
            return (Criteria) this;
        }

        public Criteria andUuidIsNotNull() {
            addCriterion("uuid is not null");
            return (Criteria) this;
        }

        public Criteria andUuidEqualTo(String value) {
            addCriterion("uuid =", value, "uuid");
            return (Criteria) this;
        }

        public Criteria andUuidNotEqualTo(String value) {
            addCriterion("uuid <>", value, "uuid");
            return (Criteria) this;
        }

        public Criteria andUuidGreaterThan(String value) {
            addCriterion("uuid >", value, "uuid");
            return (Criteria) this;
        }

        public Criteria andUuidGreaterThanOrEqualTo(String value) {
            addCriterion("uuid >=", value, "uuid");
            return (Criteria) this;
        }

        public Criteria andUuidLessThan(String value) {
            addCriterion("uuid <", value, "uuid");
            return (Criteria) this;
        }

        public Criteria andUuidLessThanOrEqualTo(String value) {
            addCriterion("uuid <=", value, "uuid");
            return (Criteria) this;
        }

        public Criteria andUuidLike(String value) {
            addCriterion("uuid like", value, "uuid");
            return (Criteria) this;
        }

        public Criteria andUuidNotLike(String value) {
            addCriterion("uuid not like", value, "uuid");
            return (Criteria) this;
        }

        public Criteria andUuidIn(List<String> values) {
            addCriterion("uuid in", values, "uuid");
            return (Criteria) this;
        }

        public Criteria andUuidNotIn(List<String> values) {
            addCriterion("uuid not in", values, "uuid");
            return (Criteria) this;
        }

        public Criteria andUuidBetween(String value1, String value2) {
            addCriterion("uuid between", value1, value2, "uuid");
            return (Criteria) this;
        }

        public Criteria andUuidNotBetween(String value1, String value2) {
            addCriterion("uuid not between", value1, value2, "uuid");
            return (Criteria) this;
        }

        public Criteria andUserIdIsNull() {
            addCriterion("user_id is null");
            return (Criteria) this;
        }

        public Criteria andUserIdIsNotNull() {
            addCriterion("user_id is not null");
            return (Criteria) this;
        }

        public Criteria andUserIdEqualTo(String value) {
            addCriterion("user_id =", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdNotEqualTo(String value) {
            addCriterion("user_id <>", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdGreaterThan(String value) {
            addCriterion("user_id >", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdGreaterThanOrEqualTo(String value) {
            addCriterion("user_id >=", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdLessThan(String value) {
            addCriterion("user_id <", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdLessThanOrEqualTo(String value) {
            addCriterion("user_id <=", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdLike(String value) {
            addCriterion("user_id like", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdNotLike(String value) {
            addCriterion("user_id not like", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdIn(List<String> values) {
            addCriterion("user_id in", values, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdNotIn(List<String> values) {
            addCriterion("user_id not in", values, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdBetween(String value1, String value2) {
            addCriterion("user_id between", value1, value2, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdNotBetween(String value1, String value2) {
            addCriterion("user_id not between", value1, value2, "userId");
            return (Criteria) this;
        }

        public Criteria andFruitUuidIsNull() {
            addCriterion("fruit_uuid is null");
            return (Criteria) this;
        }

        public Criteria andFruitUuidIsNotNull() {
            addCriterion("fruit_uuid is not null");
            return (Criteria) this;
        }

        public Criteria andFruitUuidEqualTo(String value) {
            addCriterion("fruit_uuid =", value, "fruitUuid");
            return (Criteria) this;
        }

        public Criteria andFruitUuidNotEqualTo(String value) {
            addCriterion("fruit_uuid <>", value, "fruitUuid");
            return (Criteria) this;
        }

        public Criteria andFruitUuidGreaterThan(String value) {
            addCriterion("fruit_uuid >", value, "fruitUuid");
            return (Criteria) this;
        }

        public Criteria andFruitUuidGreaterThanOrEqualTo(String value) {
            addCriterion("fruit_uuid >=", value, "fruitUuid");
            return (Criteria) this;
        }

        public Criteria andFruitUuidLessThan(String value) {
            addCriterion("fruit_uuid <", value, "fruitUuid");
            return (Criteria) this;
        }

        public Criteria andFruitUuidLessThanOrEqualTo(String value) {
            addCriterion("fruit_uuid <=", value, "fruitUuid");
            return (Criteria) this;
        }

        public Criteria andFruitUuidLike(String value) {
            addCriterion("fruit_uuid like", value, "fruitUuid");
            return (Criteria) this;
        }

        public Criteria andFruitUuidNotLike(String value) {
            addCriterion("fruit_uuid not like", value, "fruitUuid");
            return (Criteria) this;
        }

        public Criteria andFruitUuidIn(List<String> values) {
            addCriterion("fruit_uuid in", values, "fruitUuid");
            return (Criteria) this;
        }

        public Criteria andFruitUuidNotIn(List<String> values) {
            addCriterion("fruit_uuid not in", values, "fruitUuid");
            return (Criteria) this;
        }

        public Criteria andFruitUuidBetween(String value1, String value2) {
            addCriterion("fruit_uuid between", value1, value2, "fruitUuid");
            return (Criteria) this;
        }

        public Criteria andFruitUuidNotBetween(String value1, String value2) {
            addCriterion("fruit_uuid not between", value1, value2, "fruitUuid");
            return (Criteria) this;
        }

        public Criteria andFruitTypeIsNull() {
            addCriterion("fruit_type is null");
            return (Criteria) this;
        }

        public Criteria andFruitTypeIsNotNull() {
            addCriterion("fruit_type is not null");
            return (Criteria) this;
        }

        public Criteria andFruitTypeEqualTo(String value) {
            addCriterion("fruit_type =", value, "fruitType");
            return (Criteria) this;
        }

        public Criteria andFruitTypeNotEqualTo(String value) {
            addCriterion("fruit_type <>", value, "fruitType");
            return (Criteria) this;
        }

        public Criteria andFruitTypeGreaterThan(String value) {
            addCriterion("fruit_type >", value, "fruitType");
            return (Criteria) this;
        }

        public Criteria andFruitTypeGreaterThanOrEqualTo(String value) {
            addCriterion("fruit_type >=", value, "fruitType");
            return (Criteria) this;
        }

        public Criteria andFruitTypeLessThan(String value) {
            addCriterion("fruit_type <", value, "fruitType");
            return (Criteria) this;
        }

        public Criteria andFruitTypeLessThanOrEqualTo(String value) {
            addCriterion("fruit_type <=", value, "fruitType");
            return (Criteria) this;
        }

        public Criteria andFruitTypeLike(String value) {
            addCriterion("fruit_type like", value, "fruitType");
            return (Criteria) this;
        }

        public Criteria andFruitTypeNotLike(String value) {
            addCriterion("fruit_type not like", value, "fruitType");
            return (Criteria) this;
        }

        public Criteria andFruitTypeIn(List<String> values) {
            addCriterion("fruit_type in", values, "fruitType");
            return (Criteria) this;
        }

        public Criteria andFruitTypeNotIn(List<String> values) {
            addCriterion("fruit_type not in", values, "fruitType");
            return (Criteria) this;
        }

        public Criteria andFruitTypeBetween(String value1, String value2) {
            addCriterion("fruit_type between", value1, value2, "fruitType");
            return (Criteria) this;
        }

        public Criteria andFruitTypeNotBetween(String value1, String value2) {
            addCriterion("fruit_type not between", value1, value2, "fruitType");
            return (Criteria) this;
        }

        public Criteria andOperateTypeIsNull() {
            addCriterion("operate_type is null");
            return (Criteria) this;
        }

        public Criteria andOperateTypeIsNotNull() {
            addCriterion("operate_type is not null");
            return (Criteria) this;
        }

        public Criteria andOperateTypeEqualTo(String value) {
            addCriterion("operate_type =", value, "operateType");
            return (Criteria) this;
        }

        public Criteria andOperateTypeNotEqualTo(String value) {
            addCriterion("operate_type <>", value, "operateType");
            return (Criteria) this;
        }

        public Criteria andOperateTypeGreaterThan(String value) {
            addCriterion("operate_type >", value, "operateType");
            return (Criteria) this;
        }

        public Criteria andOperateTypeGreaterThanOrEqualTo(String value) {
            addCriterion("operate_type >=", value, "operateType");
            return (Criteria) this;
        }

        public Criteria andOperateTypeLessThan(String value) {
            addCriterion("operate_type <", value, "operateType");
            return (Criteria) this;
        }

        public Criteria andOperateTypeLessThanOrEqualTo(String value) {
            addCriterion("operate_type <=", value, "operateType");
            return (Criteria) this;
        }

        public Criteria andOperateTypeLike(String value) {
            addCriterion("operate_type like", value, "operateType");
            return (Criteria) this;
        }

        public Criteria andOperateTypeNotLike(String value) {
            addCriterion("operate_type not like", value, "operateType");
            return (Criteria) this;
        }

        public Criteria andOperateTypeIn(List<String> values) {
            addCriterion("operate_type in", values, "operateType");
            return (Criteria) this;
        }

        public Criteria andOperateTypeNotIn(List<String> values) {
            addCriterion("operate_type not in", values, "operateType");
            return (Criteria) this;
        }

        public Criteria andOperateTypeBetween(String value1, String value2) {
            addCriterion("operate_type between", value1, value2, "operateType");
            return (Criteria) this;
        }

        public Criteria andOperateTypeNotBetween(String value1, String value2) {
            addCriterion("operate_type not between", value1, value2, "operateType");
            return (Criteria) this;
        }

        public Criteria andModifyDateTimeIsNull() {
            addCriterion("modify_date_time is null");
            return (Criteria) this;
        }

        public Criteria andModifyDateTimeIsNotNull() {
            addCriterion("modify_date_time is not null");
            return (Criteria) this;
        }

        public Criteria andModifyDateTimeEqualTo(Date value) {
            addCriterion("modify_date_time =", value, "modifyDateTime");
            return (Criteria) this;
        }

        public Criteria andModifyDateTimeNotEqualTo(Date value) {
            addCriterion("modify_date_time <>", value, "modifyDateTime");
            return (Criteria) this;
        }

        public Criteria andModifyDateTimeGreaterThan(Date value) {
            addCriterion("modify_date_time >", value, "modifyDateTime");
            return (Criteria) this;
        }

        public Criteria andModifyDateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("modify_date_time >=", value, "modifyDateTime");
            return (Criteria) this;
        }

        public Criteria andModifyDateTimeLessThan(Date value) {
            addCriterion("modify_date_time <", value, "modifyDateTime");
            return (Criteria) this;
        }

        public Criteria andModifyDateTimeLessThanOrEqualTo(Date value) {
            addCriterion("modify_date_time <=", value, "modifyDateTime");
            return (Criteria) this;
        }

        public Criteria andModifyDateTimeIn(List<Date> values) {
            addCriterion("modify_date_time in", values, "modifyDateTime");
            return (Criteria) this;
        }

        public Criteria andModifyDateTimeNotIn(List<Date> values) {
            addCriterion("modify_date_time not in", values, "modifyDateTime");
            return (Criteria) this;
        }

        public Criteria andModifyDateTimeBetween(Date value1, Date value2) {
            addCriterion("modify_date_time between", value1, value2, "modifyDateTime");
            return (Criteria) this;
        }

        public Criteria andModifyDateTimeNotBetween(Date value1, Date value2) {
            addCriterion("modify_date_time not between", value1, value2, "modifyDateTime");
            return (Criteria) this;
        }

        public Criteria andCreateDateTimeIsNull() {
            addCriterion("create_date_time is null");
            return (Criteria) this;
        }

        public Criteria andCreateDateTimeIsNotNull() {
            addCriterion("create_date_time is not null");
            return (Criteria) this;
        }

        public Criteria andCreateDateTimeEqualTo(Date value) {
            addCriterion("create_date_time =", value, "createDateTime");
            return (Criteria) this;
        }

        public Criteria andCreateDateTimeNotEqualTo(Date value) {
            addCriterion("create_date_time <>", value, "createDateTime");
            return (Criteria) this;
        }

        public Criteria andCreateDateTimeGreaterThan(Date value) {
            addCriterion("create_date_time >", value, "createDateTime");
            return (Criteria) this;
        }

        public Criteria andCreateDateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("create_date_time >=", value, "createDateTime");
            return (Criteria) this;
        }

        public Criteria andCreateDateTimeLessThan(Date value) {
            addCriterion("create_date_time <", value, "createDateTime");
            return (Criteria) this;
        }

        public Criteria andCreateDateTimeLessThanOrEqualTo(Date value) {
            addCriterion("create_date_time <=", value, "createDateTime");
            return (Criteria) this;
        }

        public Criteria andCreateDateTimeIn(List<Date> values) {
            addCriterion("create_date_time in", values, "createDateTime");
            return (Criteria) this;
        }

        public Criteria andCreateDateTimeNotIn(List<Date> values) {
            addCriterion("create_date_time not in", values, "createDateTime");
            return (Criteria) this;
        }

        public Criteria andCreateDateTimeBetween(Date value1, Date value2) {
            addCriterion("create_date_time between", value1, value2, "createDateTime");
            return (Criteria) this;
        }

        public Criteria andCreateDateTimeNotBetween(Date value1, Date value2) {
            addCriterion("create_date_time not between", value1, value2, "createDateTime");
            return (Criteria) this;
        }

        public Criteria andIsDeletedIsNull() {
            addCriterion("is_deleted is null");
            return (Criteria) this;
        }

        public Criteria andIsDeletedIsNotNull() {
            addCriterion("is_deleted is not null");
            return (Criteria) this;
        }

        public Criteria andIsDeletedEqualTo(String value) {
            addCriterion("is_deleted =", value, "isDeleted");
            return (Criteria) this;
        }

        public Criteria andIsDeletedNotEqualTo(String value) {
            addCriterion("is_deleted <>", value, "isDeleted");
            return (Criteria) this;
        }

        public Criteria andIsDeletedGreaterThan(String value) {
            addCriterion("is_deleted >", value, "isDeleted");
            return (Criteria) this;
        }

        public Criteria andIsDeletedGreaterThanOrEqualTo(String value) {
            addCriterion("is_deleted >=", value, "isDeleted");
            return (Criteria) this;
        }

        public Criteria andIsDeletedLessThan(String value) {
            addCriterion("is_deleted <", value, "isDeleted");
            return (Criteria) this;
        }

        public Criteria andIsDeletedLessThanOrEqualTo(String value) {
            addCriterion("is_deleted <=", value, "isDeleted");
            return (Criteria) this;
        }

        public Criteria andIsDeletedLike(String value) {
            addCriterion("is_deleted like", value, "isDeleted");
            return (Criteria) this;
        }

        public Criteria andIsDeletedNotLike(String value) {
            addCriterion("is_deleted not like", value, "isDeleted");
            return (Criteria) this;
        }

        public Criteria andIsDeletedIn(List<String> values) {
            addCriterion("is_deleted in", values, "isDeleted");
            return (Criteria) this;
        }

        public Criteria andIsDeletedNotIn(List<String> values) {
            addCriterion("is_deleted not in", values, "isDeleted");
            return (Criteria) this;
        }

        public Criteria andIsDeletedBetween(String value1, String value2) {
            addCriterion("is_deleted between", value1, value2, "isDeleted");
            return (Criteria) this;
        }

        public Criteria andIsDeletedNotBetween(String value1, String value2) {
            addCriterion("is_deleted not between", value1, value2, "isDeleted");
            return (Criteria) this;
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table fruits_logs
     *
     * @mbg.generated do_not_delete_during_merge Thu Dec 21 19:20:22 CST 2017
     */
    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table fruits_logs
     *
     * @mbg.generated Thu Dec 21 19:20:22 CST 2017
     */
    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}