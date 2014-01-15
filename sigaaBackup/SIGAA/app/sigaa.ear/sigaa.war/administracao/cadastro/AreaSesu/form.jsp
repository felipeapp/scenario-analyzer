<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Area da Sesu</h2>

	<center>
			<h:messages/>
			<h:form>
			<div class="infoAltRem" style="text-align: center; width: 100%">
				<h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  			<h:commandLink value="Listar" action="#{areaSesu.listar}"/>
			</div>
			</h:form>
	</center>

	<table class=formulario width="80%">
		<h:form>
			<caption class="listagem">Cadastro de Área Sesu</caption>
			<h:inputHidden value="#{areaSesu.confirmButton}" />
			<h:inputHidden value="#{areaSesu.obj.id}" />
			<tr>
				<th width="25%" class="required">Código:</th>
				<td><h:inputText size="8" maxlength="12"
					value="#{areaSesu.obj.codigo}" readonly="#{areaSesu.readOnly}" /></td>
			</tr>
			<tr>
				<th class="required">Nome:</th>
				<td> 
					<h:inputText maxlength="200" value="#{areaSesu.obj.nome}" readonly="#{areaSesu.readOnly}" style="width: 95%;"/>
				</td>
			</tr>
			<tr>
				<th class="required">Duração Padrão:</th>
				<td>
					<h:inputText size="8" maxlength="2" value="#{areaSesu.obj.duracaoPadrao}" onkeyup="return formatarInteiro(this);" readonly="#{areaSesu.readOnly}">
					</h:inputText>		
				</td>
			</tr>
			<tr>
				<th class="required">Fator de Retenção:</th>
				<td>
					<h:inputText size="8" maxlength="20" value="#{areaSesu.obj.fatorRetencao}" onfocus="javascript:select()" onkeydown="return(formataValor(this, event, 4))" readonly="#{areaSesu.readOnly}">
						<f:convertNumber minFractionDigits="4" />
					</h:inputText>
				</td>
			</tr>
			<tr>
				<th class="required">Peso do Grupo:</th>
				<td><h:inputText size="8" maxlength="6" value="#{areaSesu.obj.pesoGrupo}" onfocus="javascript:select()" onkeydown="return formataValor(this, event, 1)" readonly="#{areaSesu.readOnly}">
						<f:convertNumber minFractionDigits="1"/>
					</h:inputText>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{areaSesu.confirmButton}" action="#{areaSesu.cadastrar}" />
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{areaSesu.cancelar}" /></td>
				</tr>
			</tfoot>
		</h:form>
	</table>
		<br>
	<center><h:graphicImage url="/img/required.gif"/> 
		<span class="fontePequena"> Campos de preenchimento obrigatório.</span>
	</center>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>