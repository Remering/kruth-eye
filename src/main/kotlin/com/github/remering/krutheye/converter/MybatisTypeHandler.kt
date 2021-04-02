package com.github.remering.krutheye.converter

import com.github.remering.krutheye.bean.TextureHash
import com.github.remering.krutheye.bean.TextureModel
import com.github.remering.krutheye.bean.YggdrasilUUID
import org.apache.ibatis.type.*
import java.net.InetAddress
import java.sql.CallableStatement
import java.sql.PreparedStatement
import java.sql.ResultSet



abstract class ByteArrayHandler<T>: BaseTypeHandler<T>() {

    abstract fun fromBytes(bytes: ByteArray): T?

    abstract fun toBytes(obj: T): ByteArray

    override fun setNonNullParameter(ps: PreparedStatement, i: Int, parameter: T, jdbcType: JdbcType?)
        = ps.setBytes(i, toBytes(parameter))

    override fun getNullableResult(rs: ResultSet, columnName: String) = fromBytes(rs.getBytes(columnName))

    override fun getNullableResult(rs: ResultSet, columnIndex: Int) = fromBytes(rs.getBytes(columnIndex))

    override fun getNullableResult(cs: CallableStatement, columnIndex: Int) = fromBytes(cs.getBytes(columnIndex))

}

@MappedJdbcTypes(JdbcType.BINARY)
@MappedTypes(YggdrasilUUID::class)
class YggdrasilTypeHandler: ByteArrayHandler<YggdrasilUUID>() {

    private val serializer = YggdrasilUUIDRedisSerializer()

    override fun fromBytes(bytes: ByteArray) = serializer.deserialize(bytes)

    override fun toBytes(obj: YggdrasilUUID) = serializer.serialize(obj)!!

}

@MappedTypes(TextureModel::class)
class TextureModelHandler: BaseTypeHandler<TextureModel>() {

    override fun setNonNullParameter(ps: PreparedStatement, i: Int, parameter: TextureModel, jdbcType: JdbcType?) {
        ps.setString(i, parameter.model)

    }

    private fun getTextureModel(model: String): TextureModel
        = when(model) {
            TextureModel.STEVE.model -> TextureModel.STEVE
            TextureModel.ALEX.model -> TextureModel.ALEX
            else -> throw IllegalArgumentException("Can not map '${model}' to TextureModel")
        }
    override fun getNullableResult(rs: ResultSet, columnName: String)
        = getTextureModel(rs.getString(columnName))

    override fun getNullableResult(rs: ResultSet, columnIndex: Int)
        = getTextureModel(rs.getString(columnIndex))

    override fun getNullableResult(cs: CallableStatement, columnIndex: Int)
        = getTextureModel(cs.getString(columnIndex))

}

@MappedTypes(InetAddress::class)
@MappedJdbcTypes(JdbcType.BINARY)
class InetAddressHandler: ByteArrayHandler<InetAddress>() {

    override fun fromBytes(bytes: ByteArray) = InetAddress.getByAddress(bytes)!!

    override fun toBytes(obj: InetAddress) = obj.address!!
}

@MappedTypes(TextureHash::class)
@MappedJdbcTypes(JdbcType.BINARY)
class TextureHashHandler: ByteArrayHandler<TextureHash>() {

    override fun fromBytes(bytes: ByteArray) = TextureHash(bytes)

    override fun toBytes(obj: TextureHash) = obj.hashBytes
}