package com.tu.employeemanagement.misc;

// the app's fixed texts and numbers in one place, grouped by kind. The nested classes are static, so they're used like
// namespaces: import ...EmployeeManagementConstants.ExceptionMessages, then write ExceptionMessages.EMPLOYEE_NOT_FOUND.
// They only hold constants, so every class here is final with a private constructor: none can be extended or instantiated
public final class EmployeeManagementConstants {

    private EmployeeManagementConstants() {
    }

    // thrown by the services and the seeders; %d and %s are placeholders, e.g. EMPLOYEE_NOT_FOUND.formatted(id)
    public static final class ExceptionMessages {

        public static final String DEPARTMENT_NOT_FOUND = "Department %d not found";
        public static final String DEPARTMENT_NAME_IN_USE = "Department name is already in use";
        public static final String DEPARTMENT_HAS_EMPLOYEES = "Department still has employees";

        public static final String EMPLOYEE_NOT_FOUND = "Employee %d not found";
        public static final String EMPLOYEE_EMAIL_IN_USE = "Employee email is already in use";
        public static final String EMPLOYEE_PHONE_NUMBER_IN_USE = "Employee phone number is already in use";

        public static final String VALIDATION_FAILED = "Validation failed";
        public static final String SEED_DEPARTMENT_MISSING = "Cannot seed employees: department '%s' does not exist";

        private ExceptionMessages() {
        }

    }

    // shown when @Valid fails; the validator fills in {min} and {max} from the @Size annotation
    public static final class ValidationMessages {

        public static final String FIRST_NAME_REQUIRED = "First name is required";
        public static final String FIRST_NAME_LENGTH = "First name must be between {min} and {max} characters";
        public static final String LAST_NAME_REQUIRED = "Last name is required";
        public static final String LAST_NAME_LENGTH = "Last name must be between {min} and {max} characters";
        public static final String PROFILE_PICTURE_TOO_LONG = "Profile picture URL is too long";
        public static final String PROFILE_PICTURE_INVALID = "Profile picture must be a valid http or https URL";
        public static final String PHONE_NUMBER_INVALID = "Phone number must be a valid format";
        public static final String EMAIL_REQUIRED = "Email is required";
        public static final String EMAIL_INVALID = "Email should be valid";
        public static final String DEPARTMENT_REQUIRED = "Department is required";

        public static final String DEPARTMENT_NAME_REQUIRED = "Department name is required";
        public static final String DEPARTMENT_NAME_LENGTH = "Department name must be between {min} and {max} characters";

        private ValidationMessages() {
        }

    }

    // limits the DTOs validate; the entities use the same maximum lengths for their columns, so the two can't drift apart
    public static final class ValidationRules {

        public static final int FIRST_NAME_MIN_LENGTH = 2;
        public static final int FIRST_NAME_MAX_LENGTH = 15;
        public static final int LAST_NAME_MIN_LENGTH = 2;
        public static final int LAST_NAME_MAX_LENGTH = 20;
        public static final int PROFILE_PICTURE_MAX_LENGTH = 255;
        // @URL alone would also accept other protocols, e.g. ftp://
        public static final String HTTP_URL_PATTERN = "^https?://.*";
        // E.164: "+", then 8 to 15 digits, the first of them not 0
        public static final String PHONE_NUMBER_PATTERN = "^\\+[1-9]\\d{7,14}$";
        public static final int PHONE_NUMBER_MAX_LENGTH = 16; // "+" and the 15 digits PHONE_NUMBER_PATTERN allows at most

        public static final int DEPARTMENT_NAME_MIN_LENGTH = 3;
        public static final int DEPARTMENT_NAME_MAX_LENGTH = 30;

        private ValidationRules() {
        }

    }

    // written to the console by the seeders
    public static final class LogMessages {

        public static final String DEPARTMENTS_ALREADY_EXIST = "Departments already exist. Skipping seeding.";
        public static final String DEPARTMENTS_SEEDED = "Departments seeded successfully.";
        public static final String EMPLOYEES_ALREADY_EXIST = "Employees already exist. Skipping seeding.";
        public static final String EMPLOYEES_SEEDED = "Employees seeded successfully.";

        private LogMessages() {
        }

    }

    // DepartmentSeeder creates these departments and EmployeeSeeder looks them up by name, so both use these constants
    public static final class SeedDepartments {

        public static final String HUMAN_RESOURCES = "Human Resources";
        public static final String INFORMATION_TECHNOLOGY = "Information Technology";
        public static final String FINANCE = "Finance";
        public static final String MARKETING = "Marketing";

        private SeedDepartments() {
        }

    }

    // the @Order the seeders run in: EmployeeSeeder needs the departments that DepartmentSeeder creates
    public static final class SeederOrder {

        public static final int DEPARTMENTS = 1;
        public static final int EMPLOYEES = 2;

        private SeederOrder() {
        }

    }

}
