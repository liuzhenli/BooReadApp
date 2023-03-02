package com.micoredu.reader.dao

import androidx.room.*
import com.micoredu.reader.constant.AppPattern
import com.micoredu.reader.bean.ReplaceRule
import com.micoredu.reader.utils.cnCompare
import com.micoredu.reader.utils.splitNotBlank
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


@Dao
interface ReplaceRuleDao {

    @Query("SELECT * FROM replace_rules ORDER BY sortOrder ASC")
    fun flowAll(): Flow<List<ReplaceRule>>

    @Query("SELECT * FROM replace_rules where `group` like :key or name like :key ORDER BY sortOrder ASC")
    fun flowSearch(key: String): Flow<List<ReplaceRule>>

    @Query("SELECT * FROM replace_rules where `group` like :key ORDER BY sortOrder ASC")
    fun flowGroupSearch(key: String): Flow<List<ReplaceRule>>

    @Query("select `group` from replace_rules where `group` is not null and `group` <> ''")
    fun flowGroupsUnProcessed(): Flow<List<String>>

    @get:Query("SELECT MIN(sortOrder) FROM replace_rules")
    val minOrder: Int

    @get:Query("SELECT MAX(sortOrder) FROM replace_rules")
    val maxOrder: Int

    @get:Query("SELECT * FROM replace_rules ORDER BY sortOrder ASC")
    val all: List<ReplaceRule>

    @get:Query("select distinct `group` from replace_rules where trim(`group`) <> ''")
    val allGroupsUnProcessed: List<String>

    @get:Query("SELECT * FROM replace_rules WHERE isEnabled = 1 ORDER BY sortOrder ASC")
    val allEnabled: List<ReplaceRule>

    @Query("SELECT * FROM replace_rules WHERE id = :id")
    fun findById(id: Long): ReplaceRule?

    @Query("SELECT * FROM replace_rules WHERE id in (:ids)")
    fun findByIds(vararg ids: Long): List<ReplaceRule>

    @Query(
        """SELECT * FROM replace_rules WHERE isEnabled = 1 and scopeContent = 1
        AND (scope LIKE '%' || :name || '%' or scope LIKE '%' || :origin || '%' or scope is null or scope = '')
        order by sortOrder"""
    )
    fun findEnabledByContentScope(name: String, origin: String): List<ReplaceRule>

    @Query(
        """SELECT * FROM replace_rules WHERE isEnabled = 1 and scopeTitle = 1
        AND (scope LIKE '%' || :name || '%' or scope LIKE '%' || :origin || '%' or scope is null or scope = '')
        order by sortOrder"""
    )
    fun findEnabledByTitleScope(name: String, origin: String): List<ReplaceRule>

    @Query("select * from replace_rules where `group` like '%' || :group || '%'")
    fun getByGroup(group: String): List<ReplaceRule>

    @get:Query("select * from replace_rules where `group` is null or `group` = ''")
    val noGroup: List<ReplaceRule>

    @get:Query("SELECT COUNT(*) - SUM(isEnabled) FROM replace_rules")
    val summary: Int

    @Query("UPDATE replace_rules SET isEnabled = :enable")
    fun enableAll(enable: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg replaceRule: ReplaceRule): List<Long>

    @Update
    fun update(vararg replaceRules: ReplaceRule)

    @Delete
    fun delete(vararg replaceRules: ReplaceRule)

    private fun dealGroups(list: List<String>): List<String> {
        val groups = linkedSetOf<String>()
        list.forEach {
            it.splitNotBlank(AppPattern.splitGroupRegex).forEach { group ->
                groups.add(group)
            }
        }
        return groups.sortedWith { o1, o2 ->
            o1.cnCompare(o2)
        }
    }

    val allGroups: List<String>
        get() {
            return dealGroups(allGroupsUnProcessed)
        }

    fun flowGroups(): Flow<List<String>> {
        return flowGroupsUnProcessed().map { list ->
            dealGroups(list)
        }
    }
}