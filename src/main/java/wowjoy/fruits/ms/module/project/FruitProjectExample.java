package wowjoy.fruits.ms.module.project;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class FruitProjectExample {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table fruits_project
     *
     * @mbg.generated Mon Nov 06 15:58:00 CST 2017
     */
    protected String orderByClause;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table fruits_project
     *
     * @mbg.generated Mon Nov 06 15:58:00 CST 2017
     */
    protected boolean distinct;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table fruits_project
     *
     * @mbg.generated Mon Nov 06 15:58:00 CST 2017
     */
    protected List<Criteria> oredCriteria;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_project
     *
     * @mbg.generated Mon Nov 06 15:58:00 CST 2017
     */
    public FruitProjectExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_project
     *
     * @mbg.generated Mon Nov 06 15:58:00 CST 2017
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_project
     *
     * @mbg.generated Mon Nov 06 15:58:00 CST 2017
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_project
     *
     * @mbg.generated Mon Nov 06 15:58:00 CST 2017
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_project
     *
     * @mbg.generated Mon Nov 06 15:58:00 CST 2017
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_project
     *
     * @mbg.generated Mon Nov 06 15:58:00 CST 2017
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_project
     *
     * @mbg.generated Mon Nov 06 15:58:00 CST 2017
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_project
     *
     * @mbg.generated Mon Nov 06 15:58:00 CST 2017
     */
    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_project
     *
     * @mbg.generated Mon Nov 06 15:58:00 CST 2017
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
     * This method corresponds to the database table fruits_project
     *
     * @mbg.generated Mon Nov 06 15:58:00 CST 2017
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table fruits_project
     *
     * @mbg.generated Mon Nov 06 15:58:00 CST 2017
     */
    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table fruits_project
     *
     * @mbg.generated Mon Nov 06 15:58:00 CST 2017
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

        protected void addCriterionForJDBCDate(String condition, Date value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            addCriterion(condition, new java.sql.Date(value.getTime()), property);
        }

        protected void addCriterionForJDBCDate(String condition, List<Date> values, String property) {
            if (values == null || values.size() == 0) {
                throw new RuntimeException("Value list for " + property + " cannot be null or empty");
            }
            List<java.sql.Date> dateList = new ArrayList<java.sql.Date>();
            Iterator<Date> iter = values.iterator();
            while (iter.hasNext()) {
                dateList.add(new java.sql.Date(iter.next().getTime()));
            }
            addCriterion(condition, dateList, property);
        }

        protected void addCriterionForJDBCDate(String condition, Date value1, Date value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            addCriterion(condition, new java.sql.Date(value1.getTime()), new java.sql.Date(value2.getTime()), property);
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

        public Criteria andTitleIsNull() {
            addCriterion("title is null");
            return (Criteria) this;
        }

        public Criteria andTitleIsNotNull() {
            addCriterion("title is not null");
            return (Criteria) this;
        }

        public Criteria andTitleEqualTo(String value) {
            addCriterion("title =", value, "title");
            return (Criteria) this;
        }

        public Criteria andTitleNotEqualTo(String value) {
            addCriterion("title <>", value, "title");
            return (Criteria) this;
        }

        public Criteria andTitleGreaterThan(String value) {
            addCriterion("title >", value, "title");
            return (Criteria) this;
        }

        public Criteria andTitleGreaterThanOrEqualTo(String value) {
            addCriterion("title >=", value, "title");
            return (Criteria) this;
        }

        public Criteria andTitleLessThan(String value) {
            addCriterion("title <", value, "title");
            return (Criteria) this;
        }

        public Criteria andTitleLessThanOrEqualTo(String value) {
            addCriterion("title <=", value, "title");
            return (Criteria) this;
        }

        public Criteria andTitleLike(String value) {
            addCriterion("title like", value, "title");
            return (Criteria) this;
        }

        public Criteria andTitleNotLike(String value) {
            addCriterion("title not like", value, "title");
            return (Criteria) this;
        }

        public Criteria andTitleIn(List<String> values) {
            addCriterion("title in", values, "title");
            return (Criteria) this;
        }

        public Criteria andTitleNotIn(List<String> values) {
            addCriterion("title not in", values, "title");
            return (Criteria) this;
        }

        public Criteria andTitleBetween(String value1, String value2) {
            addCriterion("title between", value1, value2, "title");
            return (Criteria) this;
        }

        public Criteria andTitleNotBetween(String value1, String value2) {
            addCriterion("title not between", value1, value2, "title");
            return (Criteria) this;
        }

        public Criteria andProjectStatusIsNull() {
            addCriterion("project_status is null");
            return (Criteria) this;
        }

        public Criteria andProjectStatusIsNotNull() {
            addCriterion("project_status is not null");
            return (Criteria) this;
        }

        public Criteria andProjectStatusEqualTo(String value) {
            addCriterion("project_status =", value, "projectStatus");
            return (Criteria) this;
        }

        public Criteria andProjectStatusNotEqualTo(String value) {
            addCriterion("project_status <>", value, "projectStatus");
            return (Criteria) this;
        }

        public Criteria andProjectStatusGreaterThan(String value) {
            addCriterion("project_status >", value, "projectStatus");
            return (Criteria) this;
        }

        public Criteria andProjectStatusGreaterThanOrEqualTo(String value) {
            addCriterion("project_status >=", value, "projectStatus");
            return (Criteria) this;
        }

        public Criteria andProjectStatusLessThan(String value) {
            addCriterion("project_status <", value, "projectStatus");
            return (Criteria) this;
        }

        public Criteria andProjectStatusLessThanOrEqualTo(String value) {
            addCriterion("project_status <=", value, "projectStatus");
            return (Criteria) this;
        }

        public Criteria andProjectStatusLike(String value) {
            addCriterion("project_status like", value, "projectStatus");
            return (Criteria) this;
        }

        public Criteria andProjectStatusNotLike(String value) {
            addCriterion("project_status not like", value, "projectStatus");
            return (Criteria) this;
        }

        public Criteria andProjectStatusIn(List<String> values) {
            addCriterion("project_status in", values, "projectStatus");
            return (Criteria) this;
        }

        public Criteria andProjectStatusNotIn(List<String> values) {
            addCriterion("project_status not in", values, "projectStatus");
            return (Criteria) this;
        }

        public Criteria andProjectStatusBetween(String value1, String value2) {
            addCriterion("project_status between", value1, value2, "projectStatus");
            return (Criteria) this;
        }

        public Criteria andProjectStatusNotBetween(String value1, String value2) {
            addCriterion("project_status not between", value1, value2, "projectStatus");
            return (Criteria) this;
        }

        public Criteria andStatusDescriptionIsNull() {
            addCriterion("status_description is null");
            return (Criteria) this;
        }

        public Criteria andStatusDescriptionIsNotNull() {
            addCriterion("status_description is not null");
            return (Criteria) this;
        }

        public Criteria andStatusDescriptionEqualTo(String value) {
            addCriterion("status_description =", value, "statusDescription");
            return (Criteria) this;
        }

        public Criteria andStatusDescriptionNotEqualTo(String value) {
            addCriterion("status_description <>", value, "statusDescription");
            return (Criteria) this;
        }

        public Criteria andStatusDescriptionGreaterThan(String value) {
            addCriterion("status_description >", value, "statusDescription");
            return (Criteria) this;
        }

        public Criteria andStatusDescriptionGreaterThanOrEqualTo(String value) {
            addCriterion("status_description >=", value, "statusDescription");
            return (Criteria) this;
        }

        public Criteria andStatusDescriptionLessThan(String value) {
            addCriterion("status_description <", value, "statusDescription");
            return (Criteria) this;
        }

        public Criteria andStatusDescriptionLessThanOrEqualTo(String value) {
            addCriterion("status_description <=", value, "statusDescription");
            return (Criteria) this;
        }

        public Criteria andStatusDescriptionLike(String value) {
            addCriterion("status_description like", value, "statusDescription");
            return (Criteria) this;
        }

        public Criteria andStatusDescriptionNotLike(String value) {
            addCriterion("status_description not like", value, "statusDescription");
            return (Criteria) this;
        }

        public Criteria andStatusDescriptionIn(List<String> values) {
            addCriterion("status_description in", values, "statusDescription");
            return (Criteria) this;
        }

        public Criteria andStatusDescriptionNotIn(List<String> values) {
            addCriterion("status_description not in", values, "statusDescription");
            return (Criteria) this;
        }

        public Criteria andStatusDescriptionBetween(String value1, String value2) {
            addCriterion("status_description between", value1, value2, "statusDescription");
            return (Criteria) this;
        }

        public Criteria andStatusDescriptionNotBetween(String value1, String value2) {
            addCriterion("status_description not between", value1, value2, "statusDescription");
            return (Criteria) this;
        }

        public Criteria andPredictStartDateIsNull() {
            addCriterion("predict_start_date is null");
            return (Criteria) this;
        }

        public Criteria andPredictStartDateIsNotNull() {
            addCriterion("predict_start_date is not null");
            return (Criteria) this;
        }

        public Criteria andPredictStartDateEqualTo(Date value) {
            addCriterionForJDBCDate("predict_start_date =", value, "predictStartDate");
            return (Criteria) this;
        }

        public Criteria andPredictStartDateNotEqualTo(Date value) {
            addCriterionForJDBCDate("predict_start_date <>", value, "predictStartDate");
            return (Criteria) this;
        }

        public Criteria andPredictStartDateGreaterThan(Date value) {
            addCriterionForJDBCDate("predict_start_date >", value, "predictStartDate");
            return (Criteria) this;
        }

        public Criteria andPredictStartDateGreaterThanOrEqualTo(Date value) {
            addCriterionForJDBCDate("predict_start_date >=", value, "predictStartDate");
            return (Criteria) this;
        }

        public Criteria andPredictStartDateLessThan(Date value) {
            addCriterionForJDBCDate("predict_start_date <", value, "predictStartDate");
            return (Criteria) this;
        }

        public Criteria andPredictStartDateLessThanOrEqualTo(Date value) {
            addCriterionForJDBCDate("predict_start_date <=", value, "predictStartDate");
            return (Criteria) this;
        }

        public Criteria andPredictStartDateIn(List<Date> values) {
            addCriterionForJDBCDate("predict_start_date in", values, "predictStartDate");
            return (Criteria) this;
        }

        public Criteria andPredictStartDateNotIn(List<Date> values) {
            addCriterionForJDBCDate("predict_start_date not in", values, "predictStartDate");
            return (Criteria) this;
        }

        public Criteria andPredictStartDateBetween(Date value1, Date value2) {
            addCriterionForJDBCDate("predict_start_date between", value1, value2, "predictStartDate");
            return (Criteria) this;
        }

        public Criteria andPredictStartDateNotBetween(Date value1, Date value2) {
            addCriterionForJDBCDate("predict_start_date not between", value1, value2, "predictStartDate");
            return (Criteria) this;
        }

        public Criteria andPredictEndDateIsNull() {
            addCriterion("predict_end_date is null");
            return (Criteria) this;
        }

        public Criteria andPredictEndDateIsNotNull() {
            addCriterion("predict_end_date is not null");
            return (Criteria) this;
        }

        public Criteria andPredictEndDateEqualTo(Date value) {
            addCriterionForJDBCDate("predict_end_date =", value, "predictEndDate");
            return (Criteria) this;
        }

        public Criteria andPredictEndDateNotEqualTo(Date value) {
            addCriterionForJDBCDate("predict_end_date <>", value, "predictEndDate");
            return (Criteria) this;
        }

        public Criteria andPredictEndDateGreaterThan(Date value) {
            addCriterionForJDBCDate("predict_end_date >", value, "predictEndDate");
            return (Criteria) this;
        }

        public Criteria andPredictEndDateGreaterThanOrEqualTo(Date value) {
            addCriterionForJDBCDate("predict_end_date >=", value, "predictEndDate");
            return (Criteria) this;
        }

        public Criteria andPredictEndDateLessThan(Date value) {
            addCriterionForJDBCDate("predict_end_date <", value, "predictEndDate");
            return (Criteria) this;
        }

        public Criteria andPredictEndDateLessThanOrEqualTo(Date value) {
            addCriterionForJDBCDate("predict_end_date <=", value, "predictEndDate");
            return (Criteria) this;
        }

        public Criteria andPredictEndDateIn(List<Date> values) {
            addCriterionForJDBCDate("predict_end_date in", values, "predictEndDate");
            return (Criteria) this;
        }

        public Criteria andPredictEndDateNotIn(List<Date> values) {
            addCriterionForJDBCDate("predict_end_date not in", values, "predictEndDate");
            return (Criteria) this;
        }

        public Criteria andPredictEndDateBetween(Date value1, Date value2) {
            addCriterionForJDBCDate("predict_end_date between", value1, value2, "predictEndDate");
            return (Criteria) this;
        }

        public Criteria andPredictEndDateNotBetween(Date value1, Date value2) {
            addCriterionForJDBCDate("predict_end_date not between", value1, value2, "predictEndDate");
            return (Criteria) this;
        }

        public Criteria andEndDateIsNull() {
            addCriterion("end_date is null");
            return (Criteria) this;
        }

        public Criteria andEndDateIsNotNull() {
            addCriterion("end_date is not null");
            return (Criteria) this;
        }

        public Criteria andEndDateEqualTo(Date value) {
            addCriterion("end_date =", value, "endDate");
            return (Criteria) this;
        }

        public Criteria andEndDateNotEqualTo(Date value) {
            addCriterion("end_date <>", value, "endDate");
            return (Criteria) this;
        }

        public Criteria andEndDateGreaterThan(Date value) {
            addCriterion("end_date >", value, "endDate");
            return (Criteria) this;
        }

        public Criteria andEndDateGreaterThanOrEqualTo(Date value) {
            addCriterion("end_date >=", value, "endDate");
            return (Criteria) this;
        }

        public Criteria andEndDateLessThan(Date value) {
            addCriterion("end_date <", value, "endDate");
            return (Criteria) this;
        }

        public Criteria andEndDateLessThanOrEqualTo(Date value) {
            addCriterion("end_date <=", value, "endDate");
            return (Criteria) this;
        }

        public Criteria andEndDateIn(List<Date> values) {
            addCriterion("end_date in", values, "endDate");
            return (Criteria) this;
        }

        public Criteria andEndDateNotIn(List<Date> values) {
            addCriterion("end_date not in", values, "endDate");
            return (Criteria) this;
        }

        public Criteria andEndDateBetween(Date value1, Date value2) {
            addCriterion("end_date between", value1, value2, "endDate");
            return (Criteria) this;
        }

        public Criteria andEndDateNotBetween(Date value1, Date value2) {
            addCriterion("end_date not between", value1, value2, "endDate");
            return (Criteria) this;
        }

        public Criteria andDescriptionIsNull() {
            addCriterion("description is null");
            return (Criteria) this;
        }

        public Criteria andDescriptionIsNotNull() {
            addCriterion("description is not null");
            return (Criteria) this;
        }

        public Criteria andDescriptionEqualTo(String value) {
            addCriterion("description =", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotEqualTo(String value) {
            addCriterion("description <>", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionGreaterThan(String value) {
            addCriterion("description >", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionGreaterThanOrEqualTo(String value) {
            addCriterion("description >=", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionLessThan(String value) {
            addCriterion("description <", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionLessThanOrEqualTo(String value) {
            addCriterion("description <=", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionLike(String value) {
            addCriterion("description like", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotLike(String value) {
            addCriterion("description not like", value, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionIn(List<String> values) {
            addCriterion("description in", values, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotIn(List<String> values) {
            addCriterion("description not in", values, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionBetween(String value1, String value2) {
            addCriterion("description between", value1, value2, "description");
            return (Criteria) this;
        }

        public Criteria andDescriptionNotBetween(String value1, String value2) {
            addCriterion("description not between", value1, value2, "description");
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
     * This class corresponds to the database table fruits_project
     *
     * @mbg.generated do_not_delete_during_merge Mon Nov 06 15:58:00 CST 2017
     */
    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table fruits_project
     *
     * @mbg.generated Mon Nov 06 15:58:00 CST 2017
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