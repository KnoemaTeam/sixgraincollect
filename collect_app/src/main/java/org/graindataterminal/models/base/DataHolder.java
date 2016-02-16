package org.graindataterminal.models.base;

import org.graindataterminal.models.senegal.Animal;
import org.graindataterminal.models.senegal.Employee;
import org.graindataterminal.models.senegal.EmployeeLivestock;
import org.graindataterminal.models.senegal.Expenses;
import org.graindataterminal.models.senegal.FarmOwner;
import org.graindataterminal.models.senegal.Infrastructure;
import org.graindataterminal.models.senegal.LivestockEquipment;
import org.graindataterminal.models.senegal.LivestockExpenditure;
import org.graindataterminal.models.senegal.LivestockProduction;
import org.graindataterminal.models.senegal.Movement;
import org.graindataterminal.models.senegal.OperatingEquipment;
import org.graindataterminal.models.senegal.ProductionEquipment;
import org.odk.collect.android.utilities.DataUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DataHolder {
    private static DataHolder instance = null;
    private List<BaseSurvey> surveys = new ArrayList<>();

    public final static int UPDATE_TYPE_NONE = -1;
    public final static int UPDATE_TYPE_DEFAULT = 0;
    public final static int UPDATE_TYPE_BETA = 1;

    private int updateType = UPDATE_TYPE_NONE;
    private int surveysType = BaseSurvey.SURVEY_TYPE_NONE;

    private String interviewerName = null;
    private String controllerName = null;
    private String supervisorName = null;

    private BaseSurvey currentSurvey;
    private int currentSurveyIndex = 0;

    private BaseField currentField;
    private int currentFieldIndex = 0;

    private BaseCrop crop = null;
    private int cropIndex = 0;

    private OperatingEquipment operatingEquipment = null;
    private int operatingEquipmentIndex = 0;

    private Infrastructure infrastructure = null;
    private int infrastructureIndex = 0;

    private Expenses expenses = null;
    private int expensesIndex = 0;

    private Employee employee = null;
    private int employeeIndex = 0;

    private Animal animal = null;
    private int animalIndex = 0;

    private EmployeeLivestock employeeLivestock = null;
    private int employeeLivestockIndex = 0;

    private ProductionEquipment productionEquipment = null;
    private int productionEquipmentIndex = 0;

    private LivestockEquipment livestockEquipment = null;
    private int livestockEquipmentIndex = 0;

    private FarmOwner farmOwner = null;
    private int farmOwnerIndex = 0;

    private LivestockProduction livestockProduction = null;
    private int livestockProductionIndex = 0;

    private LivestockExpenditure livestockExpenditure = null;
    private int livestockExpenditureIndex = 0;

    private Movement movement = null;
    private int movementIndex = 0;

    protected DataHolder () {}

    public static DataHolder getInstance () {
        if (instance == null)
            instance = new DataHolder();

        return instance;
    }

    public void setUpdateType(int updateType) {
        this.updateType = updateType;
        DataUtils.setUpdateType(updateType);
    }

    public int getUpdateType() {
        if (updateType == UPDATE_TYPE_NONE) {
            updateType = DataUtils.getUpdateType();

            if (updateType == UPDATE_TYPE_NONE)
                updateType = UPDATE_TYPE_DEFAULT;
        }

        return updateType;
    }

    public void setInterviewerName(String interviewerName) {
        this.interviewerName = interviewerName;
        DataUtils.setInterviewerName(interviewerName);
    }

    public String getInterviewerName() {
        if (interviewerName == null)
            interviewerName = DataUtils.getInterviewerName();

        return interviewerName;
    }

    public void setControllerName(String controllerName) {
        this.controllerName = controllerName;
        DataUtils.setControllerName(controllerName);
    }

    public String getControllerName() {
        if (controllerName == null)
            controllerName = DataUtils.getControllerName();

        return controllerName;
    }

    public void setSupervisorName(String supervisorName) {
        this.supervisorName = supervisorName;
        DataUtils.setSupervisorName(supervisorName);
    }

    public String getSupervisorName() {
        if (supervisorName == null)
            supervisorName = DataUtils.getSupervisorName();

        return supervisorName;
    }

    public void setSurveysType(int surveysType) {
        this.surveysType = surveysType;
        DataUtils.setSurveysType(surveysType);
    }

    public int getSurveysType() {
        if (surveysType == BaseSurvey.SURVEY_TYPE_NONE) {
            surveysType = DataUtils.getSurveysType();

            if (surveysType == BaseSurvey.SURVEY_TYPE_NONE) {
                surveysType = BaseSurvey.SURVEY_TYPE_ZAMBIA;

                DataUtils.setSurveysType(surveysType);
            }
        }

        return surveysType;
    }

    public boolean existsSurveys () {
        return surveys != null && !surveys.isEmpty();
    }

    public List<BaseSurvey> getSurveys() {
        if (surveys.size() == 0) {
            List savedSurveys = DataUtils.getSurveyList();

            if (savedSurveys != null) {
                for (Object survey: savedSurveys)
                    surveys.add((BaseSurvey) survey);
            }
        }

        Collections.sort(surveys, new Comparator<BaseSurvey>() {
            @Override
            public int compare(BaseSurvey lhs, BaseSurvey rhs) {
                return rhs.getUpdateTime().compareTo(lhs.getUpdateTime());
            }
        });

        return surveys;
    }

    public void setCurrentSurvey(BaseSurvey currentSurvey) {
        this.currentSurvey = currentSurvey;
    }

    public BaseSurvey getCurrentSurvey() {
        if (currentSurvey == null) {
            return getCurrentSurveyByIndex(currentSurveyIndex);
        }

        return currentSurvey;
    }

    public void setCurrentSurveyIndex(int currentSurveyIndex) {
        this.currentSurveyIndex = currentSurveyIndex;
    }

    public BaseSurvey getCurrentSurveyByIndex(int position) {
        if (position <= surveys.size() - 1) {
            return surveys.get(position);
        }

        return null;
    }

    public int findSurveyIndex (String surveyId) {
        for (int i = 0; i < surveys.size(); i++) {
            if (surveys.get(i).getId().equals(surveyId))
                return i;
        }

        return -1;
    }

    protected boolean isEqualSurvey () {
        boolean isExists = false;

        for (BaseSurvey survey: surveys) {
            if (survey.getId().equals(currentSurvey.getId())) {
                isExists = true;
                break;
            }
        }

        return isExists;
    }

    public void setCurrentField(BaseField currentField) {
        this.currentField = currentField;
    }

    public BaseField getCurrentField() {
        return currentField;
    }

    public void setCurrentFieldIndex(int currentFieldIndex) {
        this.currentFieldIndex = currentFieldIndex;
    }

    public int getCurrentFieldIndex() {
        return currentFieldIndex;
    }

    public void setCrop(BaseCrop crop) {
        this.crop = crop;
    }

    public BaseCrop getCrop() {
        return crop;
    }

    public void setCropIndex(int cropIndex) {
        this.cropIndex = cropIndex;
    }

    public int getCropIndex() {
        return cropIndex;
    }

    public void setOperatingEquipment(OperatingEquipment operatingEquipment) {
        this.operatingEquipment = operatingEquipment;
    }

    public OperatingEquipment getOperatingEquipment() {
        return operatingEquipment;
    }

    public void setOperatingEquipmentIndex(int operatingEquipmentIndex) {
        this.operatingEquipmentIndex = operatingEquipmentIndex;
    }

    public int getOperatingEquipmentIndex() {
        return operatingEquipmentIndex;
    }

    public void setInfrastructure(Infrastructure infrastructure) {
        this.infrastructure = infrastructure;
    }

    public Infrastructure getInfrastructure() {
        return infrastructure;
    }

    public void setInfrastructureIndex(int infrastructureIndex) {
        this.infrastructureIndex = infrastructureIndex;
    }

    public int getInfrastructureIndex() {
        return infrastructureIndex;
    }

    public void setExpenses(Expenses expenses) {
        this.expenses = expenses;
    }

    public Expenses getExpenses() {
        return expenses;
    }

    public void setExpensesIndex(int expensesIndex) {
        this.expensesIndex = expensesIndex;
    }

    public int getExpensesIndex() {
        return expensesIndex;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployeeIndex(int employeeIndex) {
        this.employeeIndex = employeeIndex;
    }

    public int getEmployeeIndex() {
        return employeeIndex;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }

    public Animal getAnimal() {
        return animal;
    }

    public void setAnimalIndex(int animalIndex) {
        this.animalIndex = animalIndex;
    }

    public int getAnimalIndex() {
        return animalIndex;
    }

    public void setEmployeeLivestock(EmployeeLivestock employeeLivestock) {
        this.employeeLivestock = employeeLivestock;
    }

    public EmployeeLivestock getEmployeeLivestock() {
        return employeeLivestock;
    }

    public void setEmployeeLivestockIndex(int employeeLivestockIndex) {
        this.employeeLivestockIndex = employeeLivestockIndex;
    }

    public int getEmployeeLivestockIndex() {
        return employeeLivestockIndex;
    }

    public void setProductionEquipment(ProductionEquipment productionEquipment) {
        this.productionEquipment = productionEquipment;
    }

    public ProductionEquipment getProductionEquipment() {
        return productionEquipment;
    }

    public void setProductionEquipmentIndex(int productionEquipmentIndex) {
        this.productionEquipmentIndex = productionEquipmentIndex;
    }

    public int getProductionEquipmentIndex() {
        return productionEquipmentIndex;
    }

    public void setLivestockEquipment(LivestockEquipment livestockEquipment) {
        this.livestockEquipment = livestockEquipment;
    }

    public LivestockEquipment getLivestockEquipment() {
        return livestockEquipment;
    }

    public void setLivestockEquipmentIndex(int livestockEquipmentIndex) {
        this.livestockEquipmentIndex = livestockEquipmentIndex;
    }

    public int getLivestockEquipmentIndex() {
        return livestockEquipmentIndex;
    }

    public void setFarmOwner(FarmOwner farmOwner) {
        this.farmOwner = farmOwner;
    }

    public FarmOwner getFarmOwner() {
        return farmOwner;
    }

    public void setFarmOwnerIndex(int farmOwnerIndex) {
        this.farmOwnerIndex = farmOwnerIndex;
    }

    public int getFarmOwnerIndex() {
        return farmOwnerIndex;
    }

    public void setLivestockProduction(LivestockProduction livestockProduction) {
        this.livestockProduction = livestockProduction;
    }

    public LivestockProduction getLivestockProduction() {
        return livestockProduction;
    }

    public void setLivestockProductionIndex(int livestockProductionIndex) {
        this.livestockProductionIndex = livestockProductionIndex;
    }

    public int getLivestockProductionIndex() {
        return livestockProductionIndex;
    }

    public void setLivestockExpenditure(LivestockExpenditure livestockExpenditure) {
        this.livestockExpenditure = livestockExpenditure;
    }

    public LivestockExpenditure getLivestockExpenditure() {
        return livestockExpenditure;
    }

    public void setLivestockExpenditureIndex(int livestockExpenditureIndex) {
        this.livestockExpenditureIndex = livestockExpenditureIndex;
    }

    public int getLivestockExpenditureIndex() {
        return livestockExpenditureIndex;
    }

    public void setMovement(Movement movement) {
        this.movement = movement;
    }

    public Movement getMovement() {
        return movement;
    }

    public void setMovementIndex(int movementIndex) {
        this.movementIndex = movementIndex;
    }

    public int getMovementIndex() {
        return movementIndex;
    }
}
