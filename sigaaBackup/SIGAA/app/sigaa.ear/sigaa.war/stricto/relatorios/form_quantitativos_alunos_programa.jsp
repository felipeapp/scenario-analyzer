<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h:outputText value="#{relatorioQuantitativoAlunosPrograma.create}" />

<h2> <ufrn:subSistema /> > <h:outputText value="#{relatorioQuantitativoAlunosPrograma.titulo}"/> </h2>

<a4j:keepAlive beanName="relatorioQuantitativoAlunosPrograma"></a4j:keepAlive>

<h:form id="form">

<h:inputHidden value="#{relatorioQuantitativoAlunosPrograma.tipoRelatorio}"/>
<table class="formulario" style="width: 95%">
<caption> Informe os critérios para a emissão do relatório </caption>

	<c:set var="acessoReitoria" value="${false}"/>
	<ufrn:checkRole papeis="<%= new int[] {  SigaaPapeis.PORTAL_PLANEJAMENTO }  %>">
		<c:set var="acessoReitoria" value="${true}"/>
	</ufrn:checkRole>	

	<c:choose>	
	<c:when test="${!acesso.coordenadorCursoGrad && !acesso.secretarioGraduacao}">				
	<c:if test="${nivel == 'S' and relatorioQuantitativoAlunosPrograma.portalCoordenadorStricto}">	
	<input type="hidden" name="id" id="id" value="${relatorioQuantitativoAlunosPrograma.programaStricto.id}"/>
		<tr>
			<th class="rotulo" width="20%">Programa: </th>
			<td><h:outputText value="#{relatorioQuantitativoAlunosPrograma.programaStricto.sigla}"/>
				 - 
			 	<h:outputText value="#{relatorioQuantitativoAlunosPrograma.programaStricto.nome}"/>
			</td>
		</tr>
	</c:if>		
	
	<c:if test="${(nivel == 'S' and relatorioQuantitativoAlunosPrograma.portalPpg) || acessoReitoria}">	
		<tr>
			<th width="20%" class="obrigatorio">Programa:</th>
			<td><h:selectOneMenu id="programa" value="#{relatorioQuantitativoAlunosPrograma.unidade.id}" >
				<f:selectItem itemValue="-1" itemLabel="TODOS" />
				<f:selectItems value="#{unidade.allProgramaPosCombo}" />
			</h:selectOneMenu>
			</td>
		</tr>
	</c:if>		
	</c:when>
	</c:choose>	

	<c:if test="${relatorioQuantitativoAlunosPrograma.requerAnoPeriodo}">
	<h:inputHidden value="#{relatorioQuantitativoAlunosPrograma.requerAnoPeriodo}"/>
	<tr>
		<th class="required">Ano-Período: </th>
		<td>
			<c:set value="#{relatorioQuantitativoAlunosPrograma.ano}" var="anoRelatorio"></c:set>
			<input type="text" size="4" maxlength="4"  id="anoRelatorio" name="anoRelatorio" value="${anoRelatorio}"
				onkeyup="return formatarInteiro(this);">
			-
			<c:set value="#{relatorioQuantitativoAlunosPrograma.periodo}" var="periodoRelatorio"></c:set>
			<input type="text" size="1" maxlength="1" id="periodoRelatorio" name="periodoRelatorio" value="${periodoRelatorio}"
				onkeyup="return formatarInteiro(this);">
		</td>
	</tr>
	</c:if>
	<c:if test="${relatorioQuantitativoAlunosPrograma.requerTipoDiscente}">
	<h:inputHidden value="#{relatorioQuantitativoAlunosPrograma.requerTipoDiscente}"/>
	<tr>
		<th>Tipo: </th>
		<td>
			<h:selectOneMenu id="tipo" value="#{relatorioQuantitativoAlunosPrograma.tipo}">
				<f:selectItems value="#{discente.tipoCombo}"/>
			</h:selectOneMenu>
		</td>
	</tr>
	</c:if>
	<tr>
		<th class="required"> Formato do Relatório: </th>
		<td>
			<h:selectOneRadio value="#{relatorioQuantitativoAlunosPrograma.formato}" id="relatorio">
				<f:selectItem itemValue="pdf" itemLabel="PDF" />
				<f:selectItem itemValue="html" itemLabel="HTML" />
			</h:selectOneRadio>
		</td>
	</tr>

	<tfoot>
	<tr>
		<td colspan="2">
			<h:commandButton action="#{relatorioQuantitativoAlunosPrograma.gerarRelatorio}" value="Emitir Relatório" id="btnGerarRelatorio"/>
			<h:commandButton action="#{relatorioQuantitativoAlunosPrograma.cancelar}" value="Cancelar" id="btnCancelar" 
				onclick="return confirm('Deseja cancelar a operação? Todos os dados digitados não salvos serão perdidos!');"/>
		</td>
	</tr>
	</tfoot>
</table>
</h:form>

<br/>
<center>
	<html:img page="/img/required.gif" style="vertical-align: top;" /> 
	<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
</center>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>