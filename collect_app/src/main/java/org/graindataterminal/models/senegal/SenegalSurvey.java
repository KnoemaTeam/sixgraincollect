package org.graindataterminal.models.senegal;

import com.google.gson.annotations.SerializedName;
import org.graindataterminal.models.base.BaseSurvey;

import java.util.ArrayList;
import java.util.List;

public class SenegalSurvey extends BaseSurvey implements Cloneable {
    public final static String[] answerIdList = {"oui", "non"};
    public final static String[] regimeIdList = {"reg_indi", "reg_cap", "reg_coop", "reg_gie", "reg_ass", "reg_aut"};
    public final static String[] identificationIdList = {"boucle", "puc_elec", "marq_feu", "autr_ident"};
    public final static String[] creditSourceIdList = {"banque", "sfd", "projet", "tontine", "autre_source_credit"};
    public final static String[] informationSourceIdList = {"rassemblement", "radio", "journal", "internet", "autre_source_information"};

    @SerializedName("prenom_controler")
    private String controllerName;

    @SerializedName("prenom_supervisor")
    private String supervisorName;

    @SerializedName("region")
    private String region;

    @SerializedName("department")
    private String department;

    @SerializedName("arrondissement_communes")
    private String district;

    @SerializedName("rural_commune_arrondissement")
    private String town;

    @SerializedName("zone_recensement")
    private String zoneRecensement;

    @SerializedName("zone")
    private String zone;

    @SerializedName("postal")
    private String postBox;

    @SerializedName("fax")
    private String fax;

    @SerializedName("fixe_tel")
    private String phone;

    @SerializedName("cel_tel")
    private String cellPhone;

    @SerializedName("email")
    private String email;

    @SerializedName("site_web")
    private String website;

    @SerializedName("reg_juridique")
    private String legalExpRegime;

    @SerializedName("photo_chef")
    private String operatingOfficerImage;

    @SerializedName("nom_chef_exp")
    private String operatingOfficerName;

    @SerializedName("date_chef_exp")
    private String birthDate;

    @SerializedName("lieu_nais")
    private String birthPlace;

    @SerializedName("id_card")
    private String identityCardNumber;

    @SerializedName("date_del")
    private String identityCardDate;

    @SerializedName("sexe")
    private String gender;

    @SerializedName("matromonial")
    private String maritalStatus;

    @SerializedName("nationalite")
    private String nationality;

    @SerializedName("instruction")
    private String educationLevel;

    @SerializedName("other")
    private String otherEducation;

    @SerializedName("ninea")
    private String hasNINEA;

    @SerializedName("num_ninea")
    private String codeNINEA;

    @SerializedName("create_an")
    private String companyCreatedDate;

    @SerializedName("propriete")
    private String farmType;

    @SerializedName("autre_type_propriete")
    private String otherFarmType;

    @SerializedName("source_eau")
    private List<String> explotationSource;

    @SerializedName("autre_source_eau")
    private String otherExplotationSource;

    @SerializedName("source_energie")
    private List<String> powerSource;

    @SerializedName("autre_source_energie")
    private String otherPowerSource;

    @SerializedName("activite_agricole")
    private String hasAgrActivity;

    @SerializedName("org_prof")
    private String hasAgrOrganization;

    @SerializedName("organisation_agricole")
    private String agrOrganizationName;

    @SerializedName("type_sol")
    private String soilType;

    @SerializedName("autre_type_sol")
    private String otherSoilType;

    @SerializedName("relief")
    private String reliefType;

    @SerializedName("autre_relief")
    private String otherReliefType;

    @SerializedName("mode_occupation")
    private String landTenureType;

    @SerializedName("activite_exploitation")
    private List<String> operatingActivities;

    @SerializedName("source_subsistance")
    private String livelihoodSource;

    @SerializedName("nombre_parcelle")
    private String plotsNumber;

    @SerializedName("superficie_total")
    private String areaSize;

    @SerializedName("unit_mesure")
    private String areaUnit;

    @SerializedName("superficie_cultivable")
    private String cultivableAreaSize;

    @SerializedName("unit_mesure_s_t")
    private String cultivableUnit;

    @SerializedName("superficie_explitee")
    private String harvestedAreaSize;

    @SerializedName("unit_mesure_s_c")
    private String harvestedUnit;

    @SerializedName("raison_inculte")
    private List<String> uncultivatedReasons;

    @SerializedName("autre_raison")
    private String otherUncultivatedReasons;

    @SerializedName("materiel_exploitation")
    private String hasOperatingEquipment;

    @SerializedName("materiels")
    private List<OperatingEquipment> operatingEquipments;

    @SerializedName("service_use")
    private ServiceGroup serviceGroup;

    @SerializedName("infrastructure")
    private String hasInfrastructure;

    @SerializedName("infrastructure_exploitation")
    private List<Infrastructure> infrastructures;

    @SerializedName("depenses")
    private List<Expenses> expenses;

    @SerializedName("service_exp")
    private AgriculturalServicesGroup agriculturalServicesGroup;

    @SerializedName("police_assurance")
    private String hasInsurancePolicy;

    @SerializedName("assur_compagnie")
    private String insuranceCompanyName;

    @SerializedName("info_agricole")
    private List<String> agrInformation;

    @SerializedName("commerciale")
    private CommercialGroup commercialGroup;

    @SerializedName("emploi_repeat")
    private List<Employee> employees;

    @SerializedName("activte_elevage")
    private FarmingPracticeGroup farmingPracticeGroup;

    @SerializedName("caracteristique")
    private DistinguishingFeatureGroup featureGroup;

    @SerializedName("securite_cheptel")
    private SafetyStockGroup safetyStockGroup;

    @SerializedName("animaux")
    private List<Animal> animals;

    @SerializedName("emploi_elevage")
    private List<EmployeeLivestock> employeeLivestocks;

    @SerializedName("equipement")
    private String hasEquipment;

    @SerializedName("materiel_elevage")
    private List<ProductionEquipment> productionEquipments;

    @SerializedName("equipement_elevage_ferme")
    private List<LivestockEquipment> livestockEquipments;

    @SerializedName("effectif_des_especes_dans_le_ferme")
    private List<FarmOwner> farmOwners;

    @SerializedName("production")
    private List<LivestockProduction> livestockProductions;

    @SerializedName("depenses_nature_elevage")
    private List<LivestockExpenditure> livestockExpenditures;

    @SerializedName("service_elevage")
    private LivestockServicesGroup servicesGroup;

    @SerializedName("field_info")
    private List<SenegalField> fields;

    @SerializedName("mouvement")
    private List<Movement> movements;

    @SerializedName("production_cheptel")
    private TrainedProductionGroup trainedProductionGroup;

    @SerializedName("race_exploitation")
    private RacesArrestedGroup racesArrestedGroup;

    @SerializedName("alimentation")
    private PetsFoodGroup petsFoodGroup;

    @SerializedName("complementation")
    private SupplementationGroup supplementationGroup;

    @SerializedName("vaccin")
    private VaccineGroup vaccineGroup;

    @SerializedName("genetique")
    private GeneticsGroup geneticsGroup;

    @SerializedName("dest_production")
    private ProductionDestinationGroup destinationGroup;

    public SenegalSurvey() {
        super();
    }

    public SenegalSurvey clone () {
        return (SenegalSurvey) super.clone();
    }

    public void setControllerName(String controllerName) {
        this.controllerName = controllerName;
    }

    public String getControllerName() {
        return controllerName;
    }

    public void setSupervisorName(String supervisorName) {
        this.supervisorName = supervisorName;
    }

    public String getSupervisorName() {
        return supervisorName;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getRegion() {
        return region;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDepartment() {
        return department;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getDistrict() {
        return district;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getTown() {
        return town;
    }

    public void setZoneRecensement(String zoneRecensement) {
        this.zoneRecensement = zoneRecensement;
    }

    public String getZoneRecensement() {
        return zoneRecensement;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getZone() {
        return zone;
    }

    public void setPostBox(String postBox) {
        this.postBox = postBox;
    }

    public String getPostBox() {
        return postBox;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getFax() {
        return fax;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getWebsite() {
        return website;
    }

    public void setLegalExpRegime(String legalExpRegime) {
        this.legalExpRegime = legalExpRegime;

        if (legalExpRegime == null || !regimeIdList[0].equals(legalExpRegime)) {
            setOperatingOfficerImage(null);
            setHeadName(null);
            setOperatingOfficerName(null);
            setBirthDate(null);
            setBirthPlace(null);
            setIdentityCardNumber(null);
            setIdentityCardDate(null);
            setGender(null);
            setMaritalStatus(null);
            setNationality(null);
            setEducationLevel(null);
            setOtherEducation(null);
        }
    }

    public String getLegalExpRegime() {
        return legalExpRegime;
    }

    public void setOperatingOfficerImage(String operatingOfficerImage) {
        this.operatingOfficerImage = operatingOfficerImage;
    }

    public String getOperatingOfficerImage() {
        return operatingOfficerImage;
    }

    /*
    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getManagerName() {
        return managerName;
    }
    */

    public void setOperatingOfficerName(String operatingOfficerName) {
        this.operatingOfficerName = operatingOfficerName;
    }

    public String getOperatingOfficerName() {
        return operatingOfficerName;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public void setIdentityCardNumber(String identityCardNumber) {
        this.identityCardNumber = identityCardNumber;
    }

    public String getIdentityCardNumber() {
        return identityCardNumber;
    }

    public void setIdentityCardDate(String identityCardDate) {
        this.identityCardDate = identityCardDate;
    }

    public String getIdentityCardDate() {
        return identityCardDate;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGender() {
        return gender;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getNationality() {
        return nationality;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    public void setOtherEducation(String otherEducation) {
        this.otherEducation = otherEducation;
    }

    public String getOtherEducation() {
        return otherEducation;
    }

    public void setHasNINEA(String hasNINEA) {
        this.hasNINEA = hasNINEA;

        if (hasNINEA == null || answerIdList[1].equals(hasNINEA))
            setCodeNINEA(null);
    }

    public String getHasNINEA() {
        return hasNINEA;
    }

    public void setCodeNINEA(String codeNINEA) {
        this.codeNINEA = codeNINEA;
    }

    public String getCodeNINEA() {
        return codeNINEA;
    }

    public void setCompanyCreatedDate(String companyCreatedDate) {
        this.companyCreatedDate = companyCreatedDate;
    }

    public String getCompanyCreatedDate() {
        return companyCreatedDate;
    }

    public void setFarmType(String farmType) {
        this.farmType = farmType;
    }

    public String getFarmType() {
        return farmType;
    }

    public void setOtherFarmType(String otherFarmType) {
        this.otherFarmType = otherFarmType;
    }

    public String getOtherFarmType() {
        return otherFarmType;
    }

    public void setExplotationSource(List<String> explotationSource) {
        this.explotationSource = explotationSource;
    }

    public List<String> getExplotationSource() {
        if (explotationSource == null)
            explotationSource = new ArrayList<>();

        return explotationSource;
    }

    public void setOtherExplotationSource(String otherExplotationSource) {
        this.otherExplotationSource = otherExplotationSource;
    }

    public String getOtherExplotationSource() {
        return otherExplotationSource;
    }

    public void setPowerSource(List<String> powerSource) {
        this.powerSource = powerSource;
    }

    public List<String> getPowerSource() {
        if (powerSource == null)
            powerSource = new ArrayList<>();

        return powerSource;
    }

    public void setOtherPowerSource(String otherPowerSource) {
        this.otherPowerSource = otherPowerSource;
    }

    public String getOtherPowerSource() {
        return otherPowerSource;
    }

    public void setHasAgrActivity(String hasAgrActivity) {
        this.hasAgrActivity = hasAgrActivity;

        if (hasAgrActivity == null || answerIdList[1].equals(hasAgrActivity)) {
            setHasAgrOrganization(null);
            setAgrOrganizationName(null);

            setSoilType(null);
            setOtherSoilType(null);

            setReliefType(null);
            setOtherReliefType(null);

            setLandTenureType(null);
            setOperatingActivities(null);
            setLivelihoodSource(null);

            setPlotsNumber(null);

            setAreaSize(null);
            setAreaUnit(null);

            setCultivableAreaSize(null);
            setCultivableUnit(null);

            setHarvestedAreaSize(null);
            setHarvestedUnit(null);

            setUncultivatedReasons(null);
            setOtherUncultivatedReasons(null);

            setHasOperatingEquipment(null);
            setServiceGroup(null);
            setHasInfrastructure(null);
            setExpenses(null);

            setAgriculturalServicesGroup(null);
            setHasInsurancePolicy(null);
        }
    }

    public String getHasAgrActivity() {
        return hasAgrActivity;
    }

    public void setHasAgrOrganization(String hasAgrOrganization) {
        this.hasAgrOrganization = hasAgrOrganization;

        if (hasAgrOrganization == null || answerIdList[1].equals(hasAgrOrganization))
            setAgrOrganizationName(null);
    }

    public String getHasAgrOrganization() {
        return hasAgrOrganization;
    }

    public void setAgrOrganizationName(String agrOrganizationName) {
        this.agrOrganizationName = agrOrganizationName;
    }

    public String getAgrOrganizationName() {
        return agrOrganizationName;
    }

    public void setSoilType(String soilType) {
        this.soilType = soilType;
    }

    public String getSoilType() {
        return soilType;
    }

    public void setOtherSoilType(String otherSoilType) {
        this.otherSoilType = otherSoilType;
    }

    public String getOtherSoilType() {
        return otherSoilType;
    }

    public void setReliefType(String reliefType) {
        this.reliefType = reliefType;
    }

    public String getReliefType() {
        return reliefType;
    }

    public void setOtherReliefType(String otherReliefType) {
        this.otherReliefType = otherReliefType;
    }

    public String getOtherReliefType() {
        return otherReliefType;
    }

    public void setLandTenureType(String landTenureType) {
        this.landTenureType = landTenureType;
    }

    public String getLandTenureType() {
        return landTenureType;
    }

    public void setOperatingActivities(List<String> operatingActivities) {
        this.operatingActivities = operatingActivities;
    }

    public List<String> getOperatingActivities() {
        if (operatingActivities == null)
            operatingActivities = new ArrayList<>();

        return operatingActivities;
    }

    public void setLivelihoodSource(String livelihoodSource) {
        this.livelihoodSource = livelihoodSource;
    }

    public String getLivelihoodSource() {
        return livelihoodSource;
    }

    public void setPlotsNumber(String plotsNumber) {
        this.plotsNumber = plotsNumber;
    }

    public String getPlotsNumber() {
        return plotsNumber;
    }

    public void setAreaSize(String areaSize) {
        this.areaSize = areaSize;
    }

    public String getAreaSize() {
        return areaSize;
    }

    public void setAreaUnit(String areaUnit) {
        this.areaUnit = areaUnit;
    }

    public String getAreaUnit() {
        return areaUnit;
    }

    public void setCultivableAreaSize(String cultivableAreaSize) {
        this.cultivableAreaSize = cultivableAreaSize;
    }

    public String getCultivableAreaSize() {
        return cultivableAreaSize;
    }

    public void setCultivableUnit(String cultivableUnit) {
        this.cultivableUnit = cultivableUnit;
    }

    public String getCultivableUnit() {
        return cultivableUnit;
    }

    public void setHarvestedAreaSize(String harvestedAreaSize) {
        this.harvestedAreaSize = harvestedAreaSize;
    }

    public String getHarvestedAreaSize() {
        return harvestedAreaSize;
    }

    public void setHarvestedUnit(String harvestedUnit) {
        this.harvestedUnit = harvestedUnit;
    }

    public String getHarvestedUnit() {
        return harvestedUnit;
    }

    public void setUncultivatedReasons(List<String> uncultivatedReasons) {
        this.uncultivatedReasons = uncultivatedReasons;
    }

    public List<String> getUncultivatedReasons() {
        if (uncultivatedReasons == null)
            uncultivatedReasons = new ArrayList<>();

        return uncultivatedReasons;
    }

    public void setOtherUncultivatedReasons(String otherUncultivatedReasons) {
        this.otherUncultivatedReasons = otherUncultivatedReasons;
    }

    public String getOtherUncultivatedReasons() {
        return otherUncultivatedReasons;
    }

    public void setHasOperatingEquipment(String hasOperatingEquipment) {
        this.hasOperatingEquipment = hasOperatingEquipment;

        if (hasOperatingEquipment == null || answerIdList[1].equals(hasOperatingEquipment))
            setOperatingEquipments(null);
    }

    public String getHasOperatingEquipment() {
        return hasOperatingEquipment;
    }

    public void setOperatingEquipments(List<OperatingEquipment> operatingEquipments) {
        this.operatingEquipments = operatingEquipments;
    }

    public List<OperatingEquipment> getOperatingEquipments() {
        if (operatingEquipments == null)
            operatingEquipments = new ArrayList<>();

        return operatingEquipments;
    }

    public void setServiceGroup(ServiceGroup serviceGroup) {
        this.serviceGroup = serviceGroup;
    }

    public ServiceGroup getServiceGroup() {
        if (serviceGroup == null)
            serviceGroup = new ServiceGroup();

        return serviceGroup;
    }

    public void setHasInfrastructure(String hasInfrastructure) {
        this.hasInfrastructure = hasInfrastructure;

        if (hasInfrastructure == null || answerIdList[1].equals(hasInfrastructure))
            setInfrastructures(null);
    }

    public String getHasInfrastructure() {
        return hasInfrastructure;
    }

    public void setInfrastructures(List<Infrastructure> infrastructures) {
        this.infrastructures = infrastructures;
    }

    public List<Infrastructure> getInfrastructures() {
        if (infrastructures == null)
            infrastructures = new ArrayList<>();

        return infrastructures;
    }

    public void setExpenses(List<Expenses> expenses) {
        this.expenses = expenses;
    }

    public List<Expenses> getExpenses() {
        if (expenses == null)
            expenses = new ArrayList<>();

        return expenses;
    }

    public void setAgriculturalServicesGroup(AgriculturalServicesGroup agriculturalServicesGroup) {
        this.agriculturalServicesGroup = agriculturalServicesGroup;

        if (agriculturalServicesGroup != null && answerIdList[0].equals(agriculturalServicesGroup.getHasCredit())) {
            setHasInsurancePolicy(null);
            setInsuranceCompanyName(null);
        }
    }

    public AgriculturalServicesGroup getAgriculturalServicesGroup() {
        if (agriculturalServicesGroup == null)
            agriculturalServicesGroup = new AgriculturalServicesGroup();

        return agriculturalServicesGroup;
    }

    public void setHasInsurancePolicy(String hasInsurancePolicy) {
        this.hasInsurancePolicy = hasInsurancePolicy;

        if (hasInsurancePolicy == null || answerIdList[1].equals(hasInsurancePolicy))
            setInsuranceCompanyName(null);
    }

    public String getHasInsurancePolicy() {
        return hasInsurancePolicy;
    }

    public void setInsuranceCompanyName(String insuranceCompanyName) {
        this.insuranceCompanyName = insuranceCompanyName;
    }

    public String getInsuranceCompanyName() {
        return insuranceCompanyName;
    }

    public void setAgrInformation(List<String> agrInformation) {
        this.agrInformation = agrInformation;
    }

    public List<String> getAgrInformation() {
        if (agrInformation == null)
            agrInformation = new ArrayList<>();

        return agrInformation;
    }

    public void setCommercialGroup(CommercialGroup commercialGroup) {
        this.commercialGroup = commercialGroup;
    }

    public CommercialGroup getCommercialGroup() {
        if (commercialGroup == null)
            commercialGroup = new CommercialGroup();

        return commercialGroup;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public List<Employee> getEmployees() {
        if (employees == null)
            employees = new ArrayList<>();

        return employees;
    }

    public void setFarmingPracticeGroup(FarmingPracticeGroup farmingPracticeGroup) {
        this.farmingPracticeGroup = farmingPracticeGroup;

        if (farmingPracticeGroup.getIsMadeFarming() == null || answerIdList[1].equals(farmingPracticeGroup.getIsMadeFarming())) {
            setFeatureGroup(null);
            setTrainedProductionGroup(null);
            setRacesArrestedGroup(null);
            setPetsFoodGroup(null);
            setSupplementationGroup(null);
            setVaccineGroup(null);
            setGeneticsGroup(null);
            setDestinationGroup(null);
            setLivestockProductions(null);
            setFarmOwners(null);
            setHasEquipment(null);
            setLivestockExpenditures(null);
            setEmployeeLivestocks(null);
            setServicesGroup(null);
            setMovements(null);
        }
    }

    public FarmingPracticeGroup getFarmingPracticeGroup() {
        if (farmingPracticeGroup == null)
            farmingPracticeGroup = new FarmingPracticeGroup();

        return farmingPracticeGroup;
    }

    public void setFeatureGroup(DistinguishingFeatureGroup featureGroup) {
        this.featureGroup = featureGroup;
    }

    public DistinguishingFeatureGroup getFeatureGroup() {
        if (featureGroup == null)
            featureGroup = new DistinguishingFeatureGroup();

        return featureGroup;
    }

    public void setSafetyStockGroup(SafetyStockGroup safetyStockGroup) {
        this.safetyStockGroup = safetyStockGroup;
    }

    public SafetyStockGroup getSafetyStockGroup() {
        if (safetyStockGroup == null)
            safetyStockGroup = new SafetyStockGroup();

        return safetyStockGroup;
    }

    public void setAnimals(List<Animal> animals) {
        this.animals = animals;
    }

    public List<Animal> getAnimals() {
        if (animals == null)
            animals = new ArrayList<>();

        return animals;
    }

    public void setEmployeeLivestocks(List<EmployeeLivestock> employeeLivestocks) {
        this.employeeLivestocks = employeeLivestocks;
    }

    public List<EmployeeLivestock> getEmployeeLivestocks() {
        if (employeeLivestocks == null)
            employeeLivestocks = new ArrayList<>();

        return employeeLivestocks;
    }

    public void setHasEquipment(String hasEquipment) {
        this.hasEquipment = hasEquipment;

        if (hasEquipment == null || answerIdList[1].equals(hasEquipment)) {
            setProductionEquipments(null);
            setLivestockEquipments(null);
        }
    }

    public String getHasEquipment() {
        return hasEquipment;
    }

    public void setProductionEquipments(List<ProductionEquipment> productionEquipments) {
        this.productionEquipments = productionEquipments;
    }

    public List<ProductionEquipment> getProductionEquipments() {
        if (productionEquipments == null)
            productionEquipments = new ArrayList<>();

        return productionEquipments;
    }

    public void setLivestockEquipments(List<LivestockEquipment> livestockEquipments) {
        this.livestockEquipments = livestockEquipments;
    }

    public List<LivestockEquipment> getLivestockEquipments() {
        if (livestockEquipments == null)
            livestockEquipments = new ArrayList<>();

        return livestockEquipments;
    }

    public void setFarmOwners(List<FarmOwner> farmOwners) {
        this.farmOwners = farmOwners;
    }

    public List<FarmOwner> getFarmOwners() {
        if (farmOwners == null)
            farmOwners = new ArrayList<>();

        return farmOwners;
    }

    public void setLivestockProductions(List<LivestockProduction> livestockProductions) {
        this.livestockProductions = livestockProductions;
    }

    public List<LivestockProduction> getLivestockProductions() {
        if (livestockProductions == null)
            livestockProductions = new ArrayList<>();

        return livestockProductions;
    }

    public void setLivestockExpenditures(List<LivestockExpenditure> livestockExpenditures) {
        this.livestockExpenditures = livestockExpenditures;
    }

    public List<LivestockExpenditure> getLivestockExpenditures() {
        if (livestockExpenditures == null)
            livestockExpenditures = new ArrayList<>();

        return livestockExpenditures;
    }

    public void setServicesGroup(LivestockServicesGroup servicesGroup) {
        this.servicesGroup = servicesGroup;
    }

    public LivestockServicesGroup getServicesGroup() {
        if (servicesGroup == null)
            servicesGroup = new LivestockServicesGroup();

        return servicesGroup;
    }

    public void setMovements(List<Movement> movements) {
        this.movements = movements;
    }

    public List<Movement> getMovements() {
        if (movements == null)
            movements = new ArrayList<>();

        return movements;
    }

    public void setTrainedProductionGroup(TrainedProductionGroup trainedProductionGroup) {
        this.trainedProductionGroup = trainedProductionGroup;
    }

    public TrainedProductionGroup getTrainedProductionGroup() {
        if (trainedProductionGroup == null)
            trainedProductionGroup = new TrainedProductionGroup();

        return trainedProductionGroup;
    }

    public void setRacesArrestedGroup(RacesArrestedGroup racesArrestedGroup) {
        this.racesArrestedGroup = racesArrestedGroup;
    }

    public RacesArrestedGroup getRacesArrestedGroup() {
        if (racesArrestedGroup == null)
            racesArrestedGroup = new RacesArrestedGroup();

        return racesArrestedGroup;
    }

    public void setPetsFoodGroup(PetsFoodGroup petsFoodGroup) {
        this.petsFoodGroup = petsFoodGroup;
    }

    public PetsFoodGroup getPetsFoodGroup() {
        if (petsFoodGroup == null)
            petsFoodGroup = new PetsFoodGroup();

        return petsFoodGroup;
    }

    public void setSupplementationGroup(SupplementationGroup supplementationGroup) {
        this.supplementationGroup = supplementationGroup;
    }

    public SupplementationGroup getSupplementationGroup() {
        if (supplementationGroup == null)
            supplementationGroup = new SupplementationGroup();

        return supplementationGroup;
    }

    public void setVaccineGroup(VaccineGroup vaccineGroup) {
        this.vaccineGroup = vaccineGroup;
    }

    public VaccineGroup getVaccineGroup() {
        if (vaccineGroup == null)
            vaccineGroup = new VaccineGroup();

        return vaccineGroup;
    }

    public void setGeneticsGroup(GeneticsGroup geneticsGroup) {
        this.geneticsGroup = geneticsGroup;
    }

    public GeneticsGroup getGeneticsGroup() {
        if (geneticsGroup == null)
            geneticsGroup = new GeneticsGroup();

        return geneticsGroup;
    }

    public void setDestinationGroup(ProductionDestinationGroup destinationGroup) {
        this.destinationGroup = destinationGroup;
    }

    public ProductionDestinationGroup getDestinationGroup() {
        if (destinationGroup == null)
            destinationGroup = new ProductionDestinationGroup();

        return destinationGroup;
    }

    public void setFields(List<SenegalField> fields) {
        this.fields = fields;
    }

    @Override
    public List<SenegalField> getFields() {
        if (fields == null)
            fields = new ArrayList<>();

        return fields;
    }
}
