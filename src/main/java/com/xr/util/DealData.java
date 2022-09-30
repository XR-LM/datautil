package com.xr.util;

import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.util.StrUtil;

import java.io.File;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author xiaorui
 * @date 2022-09-20 20:10
 */
public class DealData {

    private static final List<String> DELETE_TABLE_NAMES = Arrays.asList("stock_change_order_queue", "stock_sale_qty_change", "stock_sale_qty_record", "fin_invoice", "fin_invoice_bill", "fin_invoice_bill_detail", "fin_invoice_detail");
    private static String skuInfoKey = "`prop_names`,`propvalue_names`,";

//    测试账号 xrpf 管理员 1
//    账套id  660126554159853568
//    管理员id  660126558198968320
//    lm 879671587836573427 grasp147+
//
//    xrpf1 管理员 1
//    账套id  701495504329834497
//    管理员id  701495504392749056
//
//
//    测试账号 cdxjxc 管理员 grasp147+
//    账套id 704378900175716353
//    管理员id 704378901064908800


    // xrpf 管理员 1
    private static String localProfileId = "704378900175716353";
    private static String localAdminId = "704378901064908800";
    //客户的账套id
    private static String userProfileId = "744966016884137985";
    //sql文件编号
    private static String sqlFileId = "4349396209960101059";
    private static String originSqlFile = "F:\\ngp-data\\" + sqlFileId + ".sql\\" + sqlFileId + ".sql";


    public static void main(String[] args) {
        System.out.println("开始处理数据");
        final File file = new File(originSqlFile);
        final File parentFile = file.getParentFile();
        final String absolutePath = parentFile.getAbsolutePath();
        final String name = file.getName();
        final FileReader fileReader = new FileReader(file, StandardCharsets.UTF_8);
        final List<String> fileList = fileReader.readLines();
        final List<String> sysData = new ArrayList<>();
        final File newFile = new File(absolutePath + "\\new" + name);
        FileWriter writer = new FileWriter(newFile);
        writer.write("");
        System.out.println("添加删除语句");
        writer.append(getDeleteSql(localProfileId));
        writer.append("\r\n");
        System.out.println("删除语句已添加完成");
        BigInteger i = new BigInteger("0");
        for (String line : fileList) {
            i = i.add(BigInteger.ONE);
            String finalLine = line;
            //替换profile_id
            finalLine = finalLine.replaceAll(userProfileId, localProfileId);
            //处理可销售库存
            final boolean deleteFlag = DELETE_TABLE_NAMES.stream().anyMatch(finalLine::contains);
            if (deleteFlag) {
                continue;
            }
            //处理sku数据
            if (finalLine.contains("base_ptype_sku")) {
                finalLine = finalLine.replaceAll(skuInfoKey, "");
                final String[] split = finalLine.split("\\('");
                StringBuilder sb = new StringBuilder();
                for (String s1 : split) {
                    String s2;
                    if (s1.contains("INSERT INTO base_ptype_sku")) {
                        sb.append(s1);
                        continue;
                    }
                    sb.append("('");
                    int j = StrUtil.ordinalIndexOf(s1, "',", 31);
                    s2 = s1.substring(0, j);
                    sb.append(s2);
                    if (s1.contains(");")) {
                        sb.append("',false,'0.00000000','0');");
                    } else {
                        sb.append("',false,'0.00000000','0'),");
                    }
                }
                finalLine = sb.toString();
            }
            //处理sysdata
            if (finalLine.contains("sys_data")) {
                sysData.add(finalLine);
                continue;
            }
            //替换管理员id
            if (finalLine.contains("base_etype")) {
                finalLine = finalLine.replaceFirst("VALUES \\('[0-9]+'", "VALUES ('" + localAdminId + "'");
            }

            finalLine = finalLine + "\r\n";
            writer.append(finalLine);
            System.out.println("开始处理第" + i + "条数据");
        }
        System.out.println("处理sysdata");
        for (String data : sysData) {
            writer.append(data + "\r\n");
        }
        System.out.println("sydata处理完毕");


        System.out.println("数据处理完毕");
    }

    public static String getDeleteSql(String profileId) {
        String s = "delete from  acc_atype_balance where profile_id= #profileId;\n" +
                "delete from  acc_atype_periodtotal where profile_id= #profileId;\n" +
                "delete from  acc_bill_account where profile_id= #profileId;\n" +
                "delete from  acc_bill_account_entry where profile_id= #profileId;\n" +
                "delete from  acc_bill_assinfo where profile_id= #profileId;\n" +
                "delete from  acc_bill_balance_detail where profile_id= #profileId;\n" +
                "delete from  acc_bill_balance_eshop_order where profile_id= #profileId;\n" +
                "delete from  acc_bill_balance_info where profile_id= #profileId;\n" +
                "delete from  acc_bill_core where profile_id= #profileId;\n" +
                "delete from  acc_bill_deliver where profile_id= #profileId;\n" +
                "delete from  acc_bill_deliver_audit where profile_id= #profileId;\n" +
                "delete from  acc_bill_deliver_distribution where profile_id= #profileId;\n" +
                "delete from  acc_bill_deliver_extend where profile_id= #profileId;\n" +
                "delete from  acc_bill_deliver_finance_audit where profile_id= #profileId;\n" +
                "delete from  acc_bill_deliver_print where profile_id= #profileId;\n" +
                "delete from  acc_bill_deliver_state where profile_id= #profileId;\n" +
                "delete from  acc_bill_deliver_wms where profile_id= #profileId;\n" +
                "delete from  acc_bill_detail_assinfo_sale where profile_id= #profileId;\n" +
                "delete from  acc_bill_detail_assinfo_stock where profile_id= #profileId;\n" +
                "delete from  acc_bill_detail_batch where profile_id= #profileId;\n" +
                "delete from  acc_bill_detail_combo where profile_id= #profileId;\n" +
                "delete from  acc_bill_detail_combo_deliver where profile_id= #profileId;\n" +
                "delete from  acc_bill_detail_combo_deliver_expand where profile_id= #profileId;\n" +
                "delete from  acc_bill_detail_core_sale where profile_id= #profileId;\n" +
                "delete from  acc_bill_detail_core_stock where profile_id= #profileId;\n" +
                "delete from  acc_bill_detail_deliver where profile_id= #profileId;\n" +
                "delete from  acc_bill_detail_deliver_expand where profile_id= #profileId;\n" +
                "delete from  acc_bill_detail_distribution where profile_id= #profileId;\n" +
                "delete from  acc_bill_detail_extend where profile_id= #profileId;\n" +
                "delete from  acc_bill_detail_position where profile_id= #profileId;\n" +
                "delete from  acc_bill_detail_purchase where profile_id= #profileId;\n" +
                "delete from  acc_bill_detail_serialno where profile_id= #profileId;\n" +
                "delete from  acc_bill_extend where profile_id= #profileId;\n" +
                "delete from  acc_bill_inout_detail where profile_id= #profileId;\n" +
                "delete from  acc_bill_inout_record where profile_id= #profileId;\n" +
                "delete from  acc_bill_invoice_info where profile_id= #profileId;\n" +
                "delete from  acc_bill_postinfo where profile_id= #profileId;\n" +
                "delete from  acc_bill_share_fee_account where profile_id= #profileId;\n" +
                "delete from  acc_bill_share_fee_to_bill where profile_id= #profileId;\n" +
                "delete from  acc_bill_share_fee_to_detail where profile_id= #profileId;\n" +
                "delete from  acc_bill_share_info where profile_id= #profileId;\n" +
                "delete from  acc_bill_vipcard_info where profile_id= #profileId;\n" +
                "delete from  acc_bill_warehouse_task where profile_id= #profileId;\n" +
                "delete from  acc_bill_warehouse_task_detail where profile_id= #profileId;\n" +
                "delete from  acc_bill_warehouse_task_detail_combo where profile_id= #profileId;\n" +
                "delete from  acc_btype_balance where profile_id= #profileId;\n" +
                "delete from  acc_business_type where profile_id= #profileId;\n" +
                "delete from  acc_check_account_group where profile_id= #profileId;\n" +
                "delete from  acc_daily_bill_account_entry where profile_id= #profileId;\n" +
                "delete from  acc_daily_bill_core where profile_id= #profileId;\n" +
                "delete from  acc_daily_bill_detail where profile_id= #profileId;\n" +
                "delete from  acc_deliver_bigdata where profile_id= #profileId;\n" +
                "delete from  acc_deliver_freight_info where profile_id= #profileId;\n" +
                "delete from  acc_deliver_mark where profile_id= #profileId;\n" +
                "delete from  acc_deliver_timing where profile_id= #profileId;\n" +
                "delete from  acc_goodsstock where profile_id= #profileId;\n" +
                "delete from  acc_goodsstock_batch where profile_id= #profileId;\n" +
                "delete from  acc_goodsstock_batchlife_bak where profile_id= #profileId;\n" +
                "delete from  acc_goodsstock_cost where profile_id= #profileId;\n" +
                "delete from  acc_goodsstock_detail where profile_id= #profileId;\n" +
                "delete from  acc_goodsstock_position where profile_id= #profileId;\n" +
                "delete from  acc_goodsstock_serialno where profile_id= #profileId;\n" +
                "delete from  acc_income_share where profile_id= #profileId;\n" +
                "delete from  acc_independent_account_entry where profile_id= #profileId;\n" +
                "delete from  acc_inigoodsstock where profile_id= #profileId;\n" +
                "delete from  acc_inigoodsstock_batch where profile_id= #profileId;\n" +
                "delete from  acc_inigoodsstock_batchlife_back where profile_id= #profileId;\n" +
                "delete from  acc_inigoodsstock_cost where profile_id= #profileId;\n" +
                "delete from  acc_inigoodsstock_detail where profile_id= #profileId;\n" +
                "delete from  acc_inigoodsstock_serialno where profile_id= #profileId;\n" +
                "delete from  acc_inventory where profile_id= #profileId;\n" +
                "delete from  acc_inventory_batch where profile_id= #profileId;\n" +
                "delete from  acc_inventory_detail where profile_id= #profileId;\n" +
                "delete from  acc_inventory_serialno where profile_id= #profileId;\n" +
                "delete from  acc_inventoryage_distribution where profile_id= #profileId;\n" +
                "delete from  acc_inventoryage_level where profile_id= #profileId;\n" +
                "delete from  acc_moving_cost where profile_id= #profileId;\n" +
                "delete from  acc_moving_cost_task where profile_id= #profileId;\n" +
                "delete from  acc_period where profile_id= #profileId;\n" +
                "delete from  acc_periodcost where profile_id= #profileId;\n" +
                "delete from  acc_periodcost_relation_period where profile_id= #profileId;\n" +
                "delete from  acc_periodcost_sku where profile_id= #profileId;\n" +
                "delete from  acc_postbill_account where profile_id= #profileId;\n" +
                "delete from  acc_postbill_account_entry where profile_id= #profileId;\n" +
                "delete from  acc_postbill_assinfo where profile_id= #profileId;\n" +
                "delete from  acc_postbill_core where profile_id= #profileId;\n" +
                "delete from  acc_postbill_detail_assinfo where profile_id= #profileId;\n" +
                "delete from  acc_postbill_detail_batch where profile_id= #profileId;\n" +
                "delete from  acc_postbill_detail_core where profile_id= #profileId;\n" +
                "delete from  acc_postbill_detail_merge where profile_id= #profileId;\n" +
                "delete from  acc_postbill_detail_serialno where profile_id= #profileId;\n" +
                "delete from  acc_postbill_hist where profile_id= #profileId;\n" +
                "delete from  acc_postbill_merge where profile_id= #profileId;\n" +
                "delete from  acc_share_record where profile_id= #profileId;\n" +
                "delete from  acc_vchtype where profile_id= #profileId;\n" +
                "delete from  acc_vchtype_business where profile_id= #profileId;\n" +
                "delete from  acc_vchtype_number where profile_id= #profileId;\n" +
                "delete from  acc_voucher where profile_id= #profileId;\n" +
                "delete from  acc_voucher_detail where profile_id= #profileId;\n" +
                "delete from  app_user_setting where profile_id= #profileId;\n" +
                "delete from  base_areatype where profile_id= #profileId;\n" +
                "delete from  base_atype where profile_id= #profileId;\n" +
                "delete from  base_atype_pay where profile_id= #profileId;\n" +
                "delete from  base_bom where profile_id= #profileId;\n" +
                "delete from  base_bom_detail where profile_id= #profileId;\n" +
                "delete from  base_brandtype where profile_id= #profileId;\n" +
                "delete from  base_btype where profile_id= #profileId;\n" +
                "delete from  base_btype_bcategory where profile_id= #profileId;\n" +
                "delete from  base_btype_contract where profile_id= #profileId;\n" +
                "delete from  base_btype_deliveryinfo where profile_id= #profileId;\n" +
                "delete from  base_btype_extend where profile_id= #profileId;\n" +
                "delete from  base_business_type where profile_id= #profileId;\n" +
                "delete from  base_car where profile_id= #profileId;\n" +
                "delete from  base_commonused_menu where profile_id= #profileId;\n" +
                "delete from  base_currency where profile_id= #profileId;\n" +
                "delete from  base_customer where profile_id= #profileId;\n" +
                "delete from  base_customer_attribute where profile_id= #profileId;\n" +
                "delete from  base_customer_contact where profile_id= #profileId;\n" +
                "delete from  base_customer_extend where profile_id= #profileId;\n" +
                "delete from  base_customer_grade where profile_id= #profileId;\n" +
                "delete from  base_customer_scale where profile_id= #profileId;\n" +
                "delete from  base_customer_tag where profile_id= #profileId;\n" +
                "delete from  base_customer_type where profile_id= #profileId;\n" +
                "delete from  base_datafield_config where profile_id= #profileId;\n" +
                "delete from  base_deliveryinfo where profile_id= #profileId;\n" +
                "delete from  base_distributor_type where profile_id= #profileId;\n" +
                "delete from  base_dtype where profile_id= #profileId;\n" +
                "delete from  base_etype where profile_id= #profileId;\n" +
                "delete from  base_fullbarcode_rule where profile_id= #profileId;\n" +
                "delete from  base_fullbarcode_rule_ptype where profile_id= #profileId;\n" +
                "delete from  base_ktype where profile_id= #profileId;\n" +
                "delete from  base_ktype_deliveryinfo where profile_id= #profileId;\n" +
                "delete from  base_ktype_position where profile_id= #profileId;\n" +
                "delete from  base_ktype_relation where profile_id= #profileId;\n" +
                "delete from  base_limit_scope where profile_id= #profileId;\n" +
                "delete from  base_limit_type where profile_id= #profileId;\n" +
                "delete from  base_line where profile_id= #profileId;\n" +
                "delete from  base_line_detail where profile_id= #profileId;\n" +
                "delete from  base_memos where profile_id= #profileId;\n" +
                "delete from  base_merge where profile_id= #profileId;\n" +
                "delete from  base_otype where profile_id= #profileId;\n" +
                "delete from  base_otypeclass where profile_id= #profileId;\n" +
                "delete from  base_payways where profile_id= #profileId;\n" +
                "delete from  base_pgroup_dimension where profile_id= #profileId;\n" +
                "delete from  base_prop where profile_id= #profileId;\n" +
                "delete from  base_prop_group where profile_id= #profileId;\n" +
                "delete from  base_prop_mapping where profile_id= #profileId;\n" +
                "delete from  base_prop_team where profile_id= #profileId;\n" +
                "delete from  base_propvalue where profile_id= #profileId;\n" +
                "delete from  base_pteam_mapping where profile_id= #profileId;\n" +
                "delete from  base_ptype where profile_id= #profileId;\n" +
                "delete from  base_ptype_btype_relation where profile_id= #profileId;\n" +
                "delete from  base_ptype_combo where profile_id= #profileId;\n" +
                "delete from  base_ptype_combo_detail where profile_id= #profileId;\n" +
                "delete from  base_ptype_discount where profile_id= #profileId;\n" +
                "delete from  base_ptype_fullbarcode where profile_id= #profileId;\n" +
                "delete from  base_ptype_ktype_relation where profile_id= #profileId;\n" +
                "delete from  base_ptype_limit_scope where profile_id= #profileId;\n" +
                "delete from  base_ptype_otype_relation where profile_id= #profileId;\n" +
                "delete from  base_ptype_pic where profile_id= #profileId;\n" +
                "delete from  base_ptype_price where profile_id= #profileId;\n" +
                "delete from  base_ptype_prop where profile_id= #profileId;\n" +
                "delete from  base_ptype_propvalue where profile_id= #profileId;\n" +
                "delete from  base_ptype_sku where profile_id= #profileId;\n" +
                "delete from  base_ptype_unit where profile_id= #profileId;\n" +
                "delete from  base_ptype_unitprice_unuse where profile_id= #profileId;\n" +
                "delete from  base_ptype_usercodes where profile_id= #profileId;\n" +
                "delete from  base_ptype_xcode where profile_id= #profileId;\n" +
                "delete from  base_sale_distribution where profile_id= #profileId;\n" +
                "delete from  base_tag where profile_id= #profileId;\n" +
                "delete from  base_track where profile_id= #profileId;\n" +
                "delete from  base_vphoto where profile_id= #profileId;\n" +
                "delete from  base_vphoto_detail where profile_id= #profileId;\n" +
                "delete from  base_vplan where profile_id= #profileId;\n" +
                "delete from  base_vplan_detail where profile_id= #profileId;\n" +
                "delete from  base_vplan_executor where profile_id= #profileId;\n" +
                "delete from  base_vrecord where profile_id= #profileId;\n" +
                "delete from  base_vrecord_bill where profile_id= #profileId;\n" +
                "delete from  base_vrecord_picture where profile_id= #profileId;\n" +
                "delete from  base_vstock where profile_id= #profileId;\n" +
                "delete from  base_vstock_detail where profile_id= #profileId;\n" +
                "delete from  base_worktime where profile_id= #profileId;\n" +
                "delete from  base_worktime_deatail where profile_id= #profileId;\n" +
                "delete from  bi_base_class where profile_id= #profileId;\n" +
                "delete from  bi_base_class_chindren where profile_id= #profileId;\n" +
                "delete from  bi_base_item where profile_id= #profileId;\n" +
                "delete from  bi_base_item_atype where profile_id= #profileId;\n" +
                "delete from  bi_base_item_ptype where profile_id= #profileId;\n" +
                "delete from  bi_collect_btype_received where profile_id= #profileId;\n" +
                "delete from  bi_collect_record where profile_id= #profileId;\n" +
                "delete from  bi_collect_summary where profile_id= #profileId;\n" +
                "delete from  bi_dtype_person where profile_id= #profileId;\n" +
                "delete from  bi_expense_entry where profile_id= #profileId;\n" +
                "delete from  bi_income_btype_received where profile_id= #profileId;\n" +
                "delete from  bi_income_entry where profile_id= #profileId;\n" +
                "delete from  cf_data_label_cpa where profile_id= #profileId;\n" +
                "delete from  cf_field_type where profile_id= #profileId;\n" +
                "delete from  cf_field_type_label where profile_id= #profileId;\n" +
                "delete from  cf_scene_field where profile_id= #profileId;\n" +
                "delete from  cold_pl_eshop_sale_order where profile_id= #profileId;\n" +
                "delete from  cold_pl_eshop_sale_order_advance_detail_distribution where profile_id= #profileId;\n" +
                "delete from  cold_pl_eshop_sale_order_advance_distribution where profile_id= #profileId;\n" +
                "delete from  cold_pl_eshop_sale_order_currency where profile_id= #profileId;\n" +
                "delete from  cold_pl_eshop_sale_order_detail where profile_id= #profileId;\n" +
                "delete from  cold_pl_eshop_sale_order_detail_currency where profile_id= #profileId;\n" +
                "delete from  cold_pl_eshop_sale_order_detail_distribution where profile_id= #profileId;\n" +
                "delete from  cold_pl_eshop_sale_order_distribution where profile_id= #profileId;\n" +
                "delete from  cold_pl_eshop_sale_order_invoice where profile_id= #profileId;\n" +
                "delete from  delivery_task where profile_id= #profileId;\n" +
                "delete from  delivery_task_detail where profile_id= #profileId;\n" +
                "delete from  fin_invoice where profile_id= #profileId;\n" +
                "delete from  fin_invoice_bill where profile_id= #profileId;\n" +
                "delete from  fin_invoice_bill_detail where profile_id= #profileId;\n" +
                "delete from  fin_invoice_config where profile_id= #profileId;\n" +
                "delete from  fin_invoice_detail where profile_id= #profileId;\n" +
                "delete from  fin_rebate_plan_core where profile_id= #profileId;\n" +
                "delete from  fin_rebate_plan_detail where profile_id= #profileId;\n" +
                "delete from  fin_rebate_plan_detail_rule where profile_id= #profileId;\n" +
                "delete from  fin_rebate_plan_detail_xtype where profile_id= #profileId;\n" +
                "delete from  fin_rebate_record_core where profile_id= #profileId;\n" +
                "delete from  fin_rebate_record_detail where profile_id= #profileId;\n" +
                "delete from  income_share_detail where profile_id= #profileId;\n" +
                "delete from  ops_allocate_cpa where profile_id= #profileId;\n" +
                "delete from  ops_allocate_record where profile_id= #profileId;\n" +
                "delete from  ops_allocate_rule where profile_id= #profileId;\n" +
                "delete from  ops_allocate_rule_bucket where profile_id= #profileId;\n" +
                "delete from  ops_allocate_rule_detail where profile_id= #profileId;\n" +
                "delete from  ops_allocate_rule_detail_text where profile_id= #profileId;\n" +
                "delete from  ops_allocate_rule_weight where profile_id= #profileId;\n" +
                "delete from  ops_cpa where profile_id= #profileId;\n" +
                "delete from  ops_cpa_belong where profile_id= #profileId;\n" +
                "delete from  ops_cpa_belong_history where profile_id= #profileId;\n" +
                "delete from  ops_cpa_extend where profile_id= #profileId;\n" +
                "delete from  ops_cpa_finfo where profile_id= #profileId;\n" +
                "delete from  ops_cpa_publicsea where profile_id= #profileId;\n" +
                "delete from  ops_cpa_publicsea_cfg where profile_id= #profileId;\n" +
                "delete from  ops_cpa_tags where profile_id= #profileId;\n" +
                "delete from  ops_proxy_cfg where profile_id= #profileId;\n" +
                "delete from  ops_select_source where profile_id= #profileId;\n" +
                "delete from  ops_task where profile_id= #profileId;\n" +
                "delete from  ops_task_communication_record where profile_id= #profileId;\n" +
                "delete from  ops_task_communication_type where profile_id= #profileId;\n" +
                "delete from  order_bill_relation where profile_id= #profileId;\n" +
                "delete from  outpay_bill_check where profile_id= #profileId;\n" +
                "delete from  pl_bill_strategy_config where profile_id= #profileId;\n" +
                "delete from  pl_buyer where profile_id= #profileId;\n" +
                "delete from  pl_customer_query where profile_id= #profileId;\n" +
                "delete from  pl_customer_query_detail where profile_id= #profileId;\n" +
                "delete from  pl_eshop where profile_id= #profileId;\n" +
                "delete from  pl_eshop_assinfo_deleted where profile_id= #profileId;\n" +
                "delete from  pl_eshop_bill_column_name_template where profile_id= #profileId;\n" +
                "delete from  pl_eshop_config where profile_id= #profileId;\n" +
                "delete from  pl_eshop_finance_check_group where profile_id= #profileId;\n" +
                "delete from  pl_eshop_ladder_default_sync_rule where profile_id= #profileId;\n" +
                "delete from  pl_eshop_notify_change where profile_id= #profileId;\n" +
                "delete from  pl_eshop_order_mark where profile_id= #profileId;\n" +
                "delete from  pl_eshop_payment_flow where profile_id= #profileId;\n" +
                "delete from  pl_eshop_payment_flow_atype_map where profile_id= #profileId;\n" +
                "delete from  pl_eshop_platform_store_mapping where profile_id= #profileId;\n" +
                "delete from  pl_eshop_product_category_rate where profile_id= #profileId;\n" +
                "delete from  pl_eshop_product_class where profile_id= #profileId;\n" +
                "delete from  pl_eshop_product_detail where profile_id= #profileId;\n" +
                "delete from  pl_eshop_product_mapping where profile_id= #profileId;\n" +
                "delete from  pl_eshop_product_mark where profile_id= #profileId;\n" +
                "delete from  pl_eshop_product_notify_change where profile_id= #profileId;\n" +
                "delete from  pl_eshop_product_publish where profile_id= #profileId;\n" +
                "delete from  pl_eshop_product_sku_mapping where profile_id= #profileId;\n" +
                "delete from  pl_eshop_product_sku_rule_config where profile_id= #profileId;\n" +
                "delete from  pl_eshop_product_sync_condition where profile_id= #profileId;\n" +
                "delete from  pl_eshop_ptype_download_task where profile_id= #profileId;\n" +
                "delete from  pl_eshop_receive_checkin_config where profile_id= #profileId;\n" +
                "delete from  pl_eshop_receive_checkin_relation where profile_id= #profileId;\n" +
                "delete from  pl_eshop_refund where profile_id= #profileId;\n" +
                "delete from  pl_eshop_refund_apply_detail where profile_id= #profileId;\n" +
                "delete from  pl_eshop_refund_apply_detail_combo where profile_id= #profileId;\n" +
                "delete from  pl_eshop_refund_apply_detail_relation where profile_id= #profileId;\n" +
                "delete from  pl_eshop_refund_apply_detail_serialno where profile_id= #profileId;\n" +
                "delete from  pl_eshop_refund_bill_releation where profile_id= #profileId;\n" +
                "delete from  pl_eshop_refund_config where profile_id= #profileId;\n" +
                "delete from  pl_eshop_refund_config_reason where profile_id= #profileId;\n" +
                "delete from  pl_eshop_refund_detail_original_cost where profile_id= #profileId;\n" +
                "delete from  pl_eshop_refund_freight where profile_id= #profileId;\n" +
                "delete from  pl_eshop_refund_opt_log where profile_id= #profileId;\n" +
                "delete from  pl_eshop_refund_pay_detail where profile_id= #profileId;\n" +
                "delete from  pl_eshop_refund_receive_checkin where profile_id= #profileId;\n" +
                "delete from  pl_eshop_refund_receive_checkin_detail where profile_id= #profileId;\n" +
                "delete from  pl_eshop_refund_receive_checkin_detail_combo where profile_id= #profileId;\n" +
                "delete from  pl_eshop_refund_receive_checkin_detail_serialno where profile_id= #profileId;\n" +
                "delete from  pl_eshop_refund_send_detail where profile_id= #profileId;\n" +
                "delete from  pl_eshop_refund_send_detail_combo where profile_id= #profileId;\n" +
                "delete from  pl_eshop_refund_send_detail_serialno where profile_id= #profileId;\n" +
                "delete from  pl_eshop_sale_order where profile_id= #profileId;\n" +
                "delete from  pl_eshop_sale_order_advance where profile_id= #profileId;\n" +
                "delete from  pl_eshop_sale_order_advance_currency where profile_id= #profileId;\n" +
                "delete from  pl_eshop_sale_order_advance_detail where profile_id= #profileId;\n" +
                "delete from  pl_eshop_sale_order_advance_detail_combo where profile_id= #profileId;\n" +
                "delete from  pl_eshop_sale_order_advance_detail_currency where profile_id= #profileId;\n" +
                "delete from  pl_eshop_sale_order_advance_detail_distribution where profile_id= #profileId;\n" +
                "delete from  pl_eshop_sale_order_advance_detail_extend where profile_id= #profileId;\n" +
                "delete from  pl_eshop_sale_order_advance_distribution where profile_id= #profileId;\n" +
                "delete from  pl_eshop_sale_order_advance_extend where profile_id= #profileId;\n" +
                "delete from  pl_eshop_sale_order_advance_invoice where profile_id= #profileId;\n" +
                "delete from  pl_eshop_sale_order_advance_timing where profile_id= #profileId;\n" +
                "delete from  pl_eshop_sale_order_change_info where profile_id= #profileId;\n" +
                "delete from  pl_eshop_sale_order_currency where profile_id= #profileId;\n" +
                "delete from  pl_eshop_sale_order_detail where profile_id= #profileId;\n" +
                "delete from  pl_eshop_sale_order_detail_combo where profile_id= #profileId;\n" +
                "delete from  pl_eshop_sale_order_detail_currency where profile_id= #profileId;\n" +
                "delete from  pl_eshop_sale_order_detail_distribution where profile_id= #profileId;\n" +
                "delete from  pl_eshop_sale_order_detail_extend where profile_id= #profileId;\n" +
                "delete from  pl_eshop_sale_order_distribution where profile_id= #profileId;\n" +
                "delete from  pl_eshop_sale_order_extend where profile_id= #profileId;\n" +
                "delete from  pl_eshop_sale_order_invoice where profile_id= #profileId;\n" +
                "delete from  pl_eshop_sale_order_settlement where profile_id= #profileId;\n" +
                "delete from  pl_eshop_sale_order_settlement_detail where profile_id= #profileId;\n" +
                "delete from  pl_eshop_sale_order_sync_condition where profile_id= #profileId;\n" +
                "delete from  pl_eshop_sale_order_timing where profile_id= #profileId;\n" +
                "delete from  pl_eshop_sale_order_to_be_matched_item where profile_id= #profileId;\n" +
                "delete from  pl_eshop_seller_class where profile_id= #profileId;\n" +
                "delete from  pl_eshop_stock_sync_default_rule where profile_id= #profileId;\n" +
                "delete from  pl_eshop_stock_sync_rule where profile_id= #profileId;\n" +
                "delete from  pl_eshop_stock_sync_rule_detail where profile_id= #profileId;\n" +
                "delete from  pl_eshop_submit_batch where profile_id= #profileId;\n" +
                "delete from  pl_eshop_submit_batch_detail where profile_id= #profileId;\n" +
                "delete from  pl_eshop_sync_rule_expand where profile_id= #profileId;\n" +
                "delete from  pl_eshop_tmc_order_msg where profile_id= #profileId;\n" +
                "delete from  pl_eshop_tmc_refund_msg where profile_id= #profileId;\n" +
                "delete from  pl_eshop_warehouse_stock_sync_rule where profile_id= #profileId;\n" +
                "delete from  pl_sale_period where profile_id= #profileId;\n" +
                "delete from  pl_sale_period_group where profile_id= #profileId;\n" +
                "delete from  pl_sale_task where profile_id= #profileId;\n" +
                "delete from  pl_sale_task_detail_btype where profile_id= #profileId;\n" +
                "delete from  pl_sale_task_detail_department where profile_id= #profileId;\n" +
                "delete from  pl_sale_task_detail_good where profile_id= #profileId;\n" +
                "delete from  pl_sale_task_detail_handle where profile_id= #profileId;\n" +
                "delete from  pl_sale_task_period_income where profile_id= #profileId;\n" +
                "delete from  pl_sender where profile_id= #profileId;\n" +
                "delete from  prnt_client_token where profile_id= #profileId;\n" +
                "delete from  prnt_default_style where profile_id= #profileId;\n" +
                "delete from  prnt_print_job where profile_id= #profileId;\n" +
                "delete from  prnt_printer where profile_id= #profileId;\n" +
                "delete from  prnt_private_style where profile_id= #profileId;\n" +
                "delete from  pub_bill_batch_save where profile_id= #profileId;\n" +
                "delete from  pub_bill_batch_save_detail where profile_id= #profileId;\n" +
                "delete from  pub_bill_default_config where profile_id= #profileId;\n" +
                "delete from  pub_bill_enclosure where profile_id= #profileId;\n" +
                "delete from  pub_bill_price where profile_id= #profileId;\n" +
                "delete from  pub_bill_print where profile_id= #profileId;\n" +
                "delete from  pub_custom_field_baseinfo where profile_id= #profileId;\n" +
                "delete from  pub_custom_field_billdetail where profile_id= #profileId;\n" +
                "delete from  pub_custom_field_billmaster where profile_id= #profileId;\n" +
                "delete from  pub_custom_field_config where profile_id= #profileId;\n" +
                "delete from  pub_loginuser_permission where profile_id= #profileId;\n" +
                "delete from  pub_loginuser_role where profile_id= #profileId;\n" +
                "delete from  pub_mobile_verifycode where profile_id= #profileId;\n" +
                "delete from  pub_msg where profile_id= #profileId;\n" +
                "delete from  pub_msg_status where profile_id= #profileId;\n" +
                "delete from  pub_role where profile_id= #profileId;\n" +
                "delete from  pub_role_permission where profile_id= #profileId;\n" +
                "delete from  ss_card where profile_id= #profileId;\n" +
                "delete from  ss_card_assert_bill where profile_id= #profileId;\n" +
                "delete from  ss_card_assert_bill_detail where profile_id= #profileId;\n" +
                "delete from  ss_card_assert_bill_vip where profile_id= #profileId;\n" +
                "delete from  ss_card_detail where profile_id= #profileId;\n" +
                "delete from  ss_card_template where profile_id= #profileId;\n" +
                "delete from  ss_card_template_detail where profile_id= #profileId;\n" +
                "delete from  ss_card_template_filter where profile_id= #profileId;\n" +
                "delete from  ss_card_template_ptype where profile_id= #profileId;\n" +
                "delete from  ss_cashbox_payment where profile_id= #profileId;\n" +
                "delete from  ss_cashier where profile_id= #profileId;\n" +
                "delete from  ss_equity_value where profile_id= #profileId;\n" +
                "delete from  ss_equity_value_detail where profile_id= #profileId;\n" +
                "delete from  ss_preferential_bill where profile_id= #profileId;\n" +
                "delete from  ss_preferential_goods_detail where profile_id= #profileId;\n" +
                "delete from  ss_promotion where profile_id= #profileId;\n" +
                "delete from  ss_promotion_otype where profile_id= #profileId;\n" +
                "delete from  ss_promotion_ptype where profile_id= #profileId;\n" +
                "delete from  ss_shift_changes_record where profile_id= #profileId;\n" +
                "delete from  ss_store where profile_id= #profileId;\n" +
                "delete from  ss_store_atype where profile_id= #profileId;\n" +
                "delete from  ss_store_cashier where profile_id= #profileId;\n" +
                "delete from  ss_store_etype where profile_id= #profileId;\n" +
                "delete from  ss_strategy where profile_id= #profileId;\n" +
                "delete from  ss_vip_asserts where profile_id= #profileId;\n" +
                "delete from  ss_vip_bill where profile_id= #profileId;\n" +
                "delete from  ss_vip_card where profile_id= #profileId;\n" +
                "delete from  ss_vip_consume where profile_id= #profileId;\n" +
                "delete from  ss_vip_consumption_record where profile_id= #profileId;\n" +
                "delete from  ss_vip_customer_expand where profile_id= #profileId;\n" +
                "delete from  ss_vip_growth_rule where profile_id= #profileId;\n" +
                "delete from  ss_vip_level where profile_id= #profileId;\n" +
                "delete from  ss_vip_level_assess_period where profile_id= #profileId;\n" +
                "delete from  ss_vip_once_card where profile_id= #profileId;\n" +
                "delete from  ss_vip_once_card_ptype_relation where profile_id= #profileId;\n" +
                "delete from  ss_vip_once_card_record where profile_id= #profileId;\n" +
                "delete from  ss_vip_once_card_validity where profile_id= #profileId;\n" +
                "delete from  ss_vip_recharge where profile_id= #profileId;\n" +
                "delete from  ss_vip_recharge_rule where profile_id= #profileId;\n" +
                "delete from  ss_vip_recharge_rule_relation where profile_id= #profileId;\n" +
                "delete from  ss_vip_score_configuration where profile_id= #profileId;\n" +
                "delete from  ss_vip_score_expire_date_record where profile_id= #profileId;\n" +
                "delete from  ss_vip_score_protect_date_record where profile_id= #profileId;\n" +
                "delete from  ss_vip_store where profile_id= #profileId;\n" +
                "delete from  ss_vip_tags where profile_id= #profileId;\n" +
                "delete from  stock_change_order_queue where profile_id= #profileId;\n" +
                "delete from  stock_datebill_qty_change where profile_id= #profileId;\n" +
                "delete from  stock_lock_qty_change where profile_id= #profileId;\n" +
                "delete from  stock_lock_qty_recalculate where profile_id= #profileId;\n" +
                "delete from  stock_lock_qty_record where profile_id= #profileId;\n" +
                "delete from  stock_qty_recalculate_log where profile_id= #profileId;\n" +
                "delete from  stock_record_index where profile_id= #profileId;\n" +
                "delete from  stock_record_order_detail where profile_id= #profileId;\n" +
                "delete from  stock_record_qty_lock where profile_id= #profileId;\n" +
                "delete from  stock_record_qty_sale where profile_id= #profileId;\n" +
                "delete from  stock_record_qty_send where profile_id= #profileId;\n" +
                "delete from  stock_record_qty_send_batch where profile_id= #profileId;\n" +
                "delete from  stock_sale_qty_change where profile_id= #profileId;\n" +
                "delete from  stock_sale_qty_recalculate where profile_id= #profileId;\n" +
                "delete from  stock_sale_qty_record where profile_id= #profileId;\n" +
                "delete from  stock_send_qty_change where profile_id= #profileId;\n" +
                "delete from  stock_send_qty_recalculate where profile_id= #profileId;\n" +
                "delete from  stock_send_qty_record where profile_id= #profileId;\n" +
                "delete from  sys_config_safelevel where profile_id= #profileId;\n" +
                "delete from  sys_config_store where profile_id= #profileId;\n" +
                "delete from  sys_data where profile_id= #profileId;\n" +
                "delete from  sys_user_data where profile_id= #profileId;\n" +
                "delete from  td_bill_account where profile_id= #profileId;\n" +
                "delete from  td_bill_assinfo where profile_id= #profileId;\n" +
                "delete from  td_bill_audit where profile_id= #profileId;\n" +
                "delete from  td_bill_audit_auditor where profile_id= #profileId;\n" +
                "delete from  td_bill_audit_auditor_detail where profile_id= #profileId;\n" +
                "delete from  td_bill_audit_conditions where profile_id= #profileId;\n" +
                "delete from  td_bill_audit_conditions_detail where profile_id= #profileId;\n" +
                "delete from  td_bill_audit_flow where profile_id= #profileId;\n" +
                "delete from  td_bill_audit_notify where profile_id= #profileId;\n" +
                "delete from  td_bill_audit_record where profile_id= #profileId;\n" +
                "delete from  td_bill_balance_detail where profile_id= #profileId;\n" +
                "delete from  td_bill_balance_eshop_order where profile_id= #profileId;\n" +
                "delete from  td_bill_balance_eshop_order_fee where profile_id= #profileId;\n" +
                "delete from  td_bill_core where profile_id= #profileId;\n" +
                "delete from  td_bill_deliver where profile_id= #profileId;\n" +
                "delete from  td_bill_deliver_audit where profile_id= #profileId;\n" +
                "delete from  td_bill_deliver_config_history where profile_id= #profileId;\n" +
                "delete from  td_bill_deliver_distribution where profile_id= #profileId;\n" +
                "delete from  td_bill_deliver_extend where profile_id= #profileId;\n" +
                "delete from  td_bill_deliver_finance_audit where profile_id= #profileId;\n" +
                "delete from  td_bill_deliver_print where profile_id= #profileId;\n" +
                "delete from  td_bill_deliver_state where profile_id= #profileId;\n" +
                "delete from  td_bill_deliver_wms where profile_id= #profileId;\n" +
                "delete from  td_bill_detail_assinfo where profile_id= #profileId;\n" +
                "delete from  td_bill_detail_batch where profile_id= #profileId;\n" +
                "delete from  td_bill_detail_combo where profile_id= #profileId;\n" +
                "delete from  td_bill_detail_combo_deliver where profile_id= #profileId;\n" +
                "delete from  td_bill_detail_combo_deliver_expand where profile_id= #profileId;\n" +
                "delete from  td_bill_detail_core where profile_id= #profileId;\n" +
                "delete from  td_bill_detail_deliver where profile_id= #profileId;\n" +
                "delete from  td_bill_detail_deliver_expand where profile_id= #profileId;\n" +
                "delete from  td_bill_detail_distribution where profile_id= #profileId;\n" +
                "delete from  td_bill_detail_extend where profile_id= #profileId;\n" +
                "delete from  td_bill_detail_position where profile_id= #profileId;\n" +
                "delete from  td_bill_detail_purchase where profile_id= #profileId;\n" +
                "delete from  td_bill_detail_serialno where profile_id= #profileId;\n" +
                "delete from  td_bill_detail_stock_release where profile_id= #profileId;\n" +
                "delete from  td_bill_extend where profile_id= #profileId;\n" +
                "delete from  td_bill_import_log where profile_id= #profileId;\n" +
                "delete from  td_bill_inout_detail where profile_id= #profileId;\n" +
                "delete from  td_bill_inout_record where profile_id= #profileId;\n" +
                "delete from  td_bill_invoice_info where profile_id= #profileId;\n" +
                "delete from  td_bill_mark where profile_id= #profileId;\n" +
                "delete from  td_bill_primary_key where profile_id= #profileId;\n" +
                "delete from  td_bill_relation where profile_id= #profileId;\n" +
                "delete from  td_bill_share_fee_account where profile_id= #profileId;\n" +
                "delete from  td_bill_share_fee_to_bill where profile_id= #profileId;\n" +
                "delete from  td_bill_share_fee_to_detail where profile_id= #profileId;\n" +
                "delete from  td_bill_vipcard_info where profile_id= #profileId;\n" +
                "delete from  td_bill_warehouse_task where profile_id= #profileId;\n" +
                "delete from  td_bill_warehouse_task_detail where profile_id= #profileId;\n" +
                "delete from  td_bill_warehouse_task_detail_combo where profile_id= #profileId;\n" +
                "delete from  td_collection_task where profile_id= #profileId;\n" +
                "delete from  td_customer_menu where profile_id= #profileId;\n" +
                "delete from  td_deliver_area_detail where profile_id= #profileId;\n" +
                "delete from  td_deliver_bigdata where profile_id= #profileId;\n" +
                "delete from  td_deliver_customer_query where profile_id= #profileId;\n" +
                "delete from  td_deliver_customer_query_detail where profile_id= #profileId;\n" +
                "delete from  td_deliver_exception_status where profile_id= #profileId;\n" +
                "delete from  td_deliver_freight_fee where profile_id= #profileId;\n" +
                "delete from  td_deliver_freight_fee_config where profile_id= #profileId;\n" +
                "delete from  td_deliver_freight_fee_config_detail where profile_id= #profileId;\n" +
                "delete from  td_deliver_freight_info where profile_id= #profileId;\n" +
                "delete from  td_deliver_freight_info_text where profile_id= #profileId;\n" +
                "delete from  td_deliver_gift_rule where profile_id= #profileId;\n" +
                "delete from  td_deliver_gift_rule_area where profile_id= #profileId;\n" +
                "delete from  td_deliver_gift_rule_give_ptype where profile_id= #profileId;\n" +
                "delete from  td_deliver_gift_rule_give_stock where profile_id= #profileId;\n" +
                "delete from  td_deliver_gift_rule_otype where profile_id= #profileId;\n" +
                "delete from  td_deliver_gift_rule_ptype where profile_id= #profileId;\n" +
                "delete from  td_deliver_gift_rule_section where profile_id= #profileId;\n" +
                "delete from  td_deliver_mark where profile_id= #profileId;\n" +
                "delete from  td_deliver_original_relation where profile_id= #profileId;\n" +
                "delete from  td_deliver_packing where profile_id= #profileId;\n" +
                "delete from  td_deliver_packing_detail where profile_id= #profileId;\n" +
                "delete from  td_deliver_packing_detail_serial where profile_id= #profileId;\n" +
                "delete from  td_deliver_print_batch where profile_id= #profileId;\n" +
                "delete from  td_deliver_print_batch_config where profile_id= #profileId;\n" +
                "delete from  td_deliver_print_batch_coordination where profile_id= #profileId;\n" +
                "delete from  td_deliver_print_batch_coordination_detail where profile_id= #profileId;\n" +
                "delete from  td_deliver_print_batch_detail where profile_id= #profileId;\n" +
                "delete from  td_deliver_print_batch_pick where profile_id= #profileId;\n" +
                "delete from  td_deliver_relation where profile_id= #profileId;\n" +
                "delete from  td_deliver_strategy where profile_id= #profileId;\n" +
                "delete from  td_deliver_strategy_detail where profile_id= #profileId;\n" +
                "delete from  td_deliver_strategy_ptype_detail where profile_id= #profileId;\n" +
                "delete from  td_deliver_timing where profile_id= #profileId;\n" +
                "delete from  td_goodsstock_safe_alarm where profile_id= #profileId;\n" +
                "delete from  td_goodsstock_safe_alarm_expand where profile_id= #profileId;\n" +
                "delete from  td_logistics_account_token where profile_id= #profileId;\n" +
                "delete from  td_logistics_template where profile_id= #profileId;\n" +
                "delete from  td_orderbill_account where profile_id= #profileId;\n" +
                "delete from  td_orderbill_assinfo where profile_id= #profileId;\n" +
                "delete from  td_orderbill_core where profile_id= #profileId;\n" +
                "delete from  td_orderbill_detail_assinfo where profile_id= #profileId;\n" +
                "delete from  td_orderbill_detail_combo where profile_id= #profileId;\n" +
                "delete from  td_orderbill_detail_core where profile_id= #profileId;\n" +
                "delete from  td_orderbill_detail_platform where profile_id= #profileId;\n" +
                "delete from  td_orderbill_detail_position where profile_id= #profileId;\n" +
                "delete from  td_orderbill_detail_serialno where profile_id= #profileId;\n" +
                "delete from  td_orderbill_platform where profile_id= #profileId;\n" +
                "delete from  td_percentage_manage_rule where profile_id= #profileId;\n" +
                "delete from  td_percentage_plan_core where profile_id= #profileId;\n" +
                "delete from  td_percentage_plan_detail where profile_id= #profileId;\n" +
                "delete from  td_percentage_record_core where profile_id= #profileId;\n" +
                "delete from  td_percentage_record_detail where profile_id= #profileId;\n" +
                "delete from  td_percentage_x_id where profile_id= #profileId;\n" +
                "delete from  td_print_style where profile_id= #profileId;\n" +
                "delete from  td_ptype_position_relation where profile_id= #profileId;\n" +
                "delete from  td_record_detail_relations where profile_id= #profileId;\n" +
                "delete from  td_record_to_bill_relation where profile_id= #profileId;\n" +
                "delete from  td_sale_esimate_config where profile_id= #profileId;\n" +
                "delete from  td_sender_config where profile_id= #profileId;\n" +
                "delete from  td_stock_check where profile_id= #profileId;\n" +
                "delete from  td_stock_check_bill where profile_id= #profileId;\n" +
                "delete from  td_stock_check_detail where profile_id= #profileId;\n" +
                "delete from  td_stock_check_serialno where profile_id= #profileId;\n" +
                "delete from  td_template where profile_id= #profileId;\n" +
                "delete from  td_template_feature_text where profile_id= #profileId;\n" +
                "delete from  td_template_printer_config where profile_id= #profileId;\n" +
                "delete from  td_vchtype_business_prefix where profile_id= #profileId;\n" +
                "delete from  td_vchtype_number where profile_id= #profileId;\n" +
                "delete from  td_vchtype_number_format where profile_id= #profileId;\n" +
                "delete from  td_vchtype_number_pool where profile_id= #profileId;\n" +
                "delete from  td_vchtype_number_rule where profile_id= #profileId;\n" +
                "delete from  td_wms_bill_confirm_core where profile_id= #profileId;\n" +
                "delete from  td_wms_bill_confirm_detail_in where profile_id= #profileId;\n" +
                "delete from  td_wms_bill_confirm_detail_out where profile_id= #profileId;\n" +
                "delete from  td_wms_bill_confirm_detail_serialno where profile_id= #profileId;\n" +
                "delete from  td_wms_bill_core where profile_id= #profileId;\n" +
                "delete from  td_wms_bill_detail_in where profile_id= #profileId;\n" +
                "delete from  td_wms_bill_detail_out where profile_id= #profileId;\n" +
                "delete from  td_wms_bill_mapper where profile_id= #profileId;\n" +
                "delete from  td_wms_eshop_mapper where profile_id= #profileId;\n" +
                "delete from  td_wms_goodsstocks where profile_id= #profileId;\n" +
                "delete from  td_wms_logistic_mapper where profile_id= #profileId;\n" +
                "delete from  td_wms_order_report where profile_id= #profileId;\n" +
                "delete from  td_wms_order_sync where profile_id= #profileId;\n" +
                "delete from  td_wms_owner where profile_id= #profileId;\n" +
                "delete from  td_wms_ptype_sync where profile_id= #profileId;\n" +
                "delete from  td_wms_stock_check where profile_id= #profileId;\n" +
                "delete from  td_wms_stock_check_bill where profile_id= #profileId;\n" +
                "delete from  td_wms_stock_mapper where profile_id= #profileId;\n" +
                "delete from  workorder_cfg_allocate where profile_id= #profileId;\n" +
                "delete from  workorder_cfg_allocate_etype where profile_id= #profileId;\n" +
                "delete from  workorder_cfg_allocate_ptype where profile_id= #profileId;\n" +
                "delete from  workorder_cfg_hresult where profile_id= #profileId;\n" +
                "delete from  workorder_cfg_level where profile_id= #profileId;\n" +
                "delete from  workorder_cfg_type where profile_id= #profileId;\n" +
                "delete from  workorder_cfg_urgent where profile_id= #profileId;\n" +
                "delete from  workorder_content where profile_id= #profileId;\n" +
                "delete from  workorder_handler where profile_id= #profileId;\n" +
                "delete from  workorder_info where profile_id= #profileId;\n" +
                "delete from  workorder_record where profile_id= #profileId;\n" +
                "delete from  workorder_relation where profile_id= #profileId;\n" +
                "delete from  workorder_reply where profile_id= #profileId;";

        return s.replaceAll("#profileId", profileId);
    }

}
