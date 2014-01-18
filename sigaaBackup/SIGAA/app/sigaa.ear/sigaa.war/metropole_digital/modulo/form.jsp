<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
<a4j:keepAlive beanName="moduloIMDMBean"/>
<h2> <ufrn:subSistema /> > ${moduloIMDMBean.descricaoOperacao} Módulo</h2>
	
<h:form id="form">
	<div class="infoAltRem">
		<html:img page="/img/adicionar.gif" style="overflow: visible;"/>: Adicionar Disciplina
	</div>
	<table class="formulario" style="width: 90%">
	  <caption>Informações do Módulo</caption>
		<h:inputHidden value="#{moduloIMDMBean.obj.id}" />
		<tbody>
			<tr>
				<th width="25%" class="obrigatorio">Descrição:</th>
				<td><h:inputText value="#{moduloIMDMBean.obj.descricao}" size="70" maxlength="50" onkeyup="CAPS(this)"
				disabled="#{moduloIMDMBean.editar}"/></td>
			</tr>
			<tr>
				<th class="obrigatorio">Carga Horária Total:</th>
				<td><h:inputText value="#{moduloIMDMBean.obj.cargaHoraria}" size="5" maxlength="4" onkeyup="return formatarInteiro(this);"/></td>
			</tr>
		  </tbody>
			<tr>
				<td colspan="2">
					<table class="subFormulario" style="width: 100%">
						<caption>Adicione Disciplinas ao Módulo</caption>
							<tr>
								<th width="25%" class="obrigatorio">Disciplina:</th>
								<td>
									<h:inputHidden id="idDisciplina" value="#{moduloIMDMBean.modulodisciplina.disciplina.id}"/>
									<h:inputText id="nomeDisciplina" value="#{moduloIMDMBean.modulodisciplina.disciplina.detalhes.nome}" size="70" />
										<ajax:autocomplete source="form:nomeDisciplina" target="form:idDisciplina"
										baseUrl="/sigaa/ajaxDisciplina" className="autocomplete" indicator="indicatorDisciplina" 
										minimumCharacters="3" parser="new ResponseXmlToHtmlListParser()" />
									<span id="indicatorDisciplina" style="display:none; "> 
										<img src="/sigaa/img/indicator.gif" /> 
									</span>
									<h:commandLink action="#{moduloIMDMBean.adicionarDisciplina}" >
										<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;" title="Adicionar Disciplina"/>
									</h:commandLink>
								</td>
							</tr>
					</table>
				</td>
			</tr>
	</table>
	
	<br /><br />
	<div class="infoAltRem">
		<html:img page="/img/delete.gif" style="overflow: visible;"/>: Remover
	</div>
		<table class="formulario" width="90%">
			<caption>Disciplinas já cadastradas para o Módulo</caption>
				<thead>
					<tr>
						<th>Disciplinas Adicionadas</th>
						<th style="text-align: right;">CH</th>
						<th></th>
					</tr>
				</thead>
			<c:set var="chTotal" value="0" />
		<c:choose>
			<c:when test="${not empty moduloIMDMBean.moduloDisciplinas}">
					<c:forEach var="linha" items="#{moduloIMDMBean.moduloDisciplinas}">
						<tr>
							<td>${linha.disciplina.detalhes.codigo} - ${linha.disciplina.detalhes.nome}</td>
							<td style="text-align: right;">${linha.disciplina.detalhes.chTotal}</td>
							<td width="5%" align="right">
								<h:commandLink action="#{moduloIMDMBean.removerDisciplina}" >
									<h:graphicImage value="/img/delete.gif" style="overflow: visible;" title="Remover" />
									<f:param name="id" value="#{linha.disciplina.id}"/>
								</h:commandLink>
							</td>
						</tr>		
					</c:forEach>
						<tr>
							<td style="text-align: right;"><b>Carga Horária Total:</b></td>
							<td style="text-align: right;">${moduloIMDMBean.obj.cargaHoraria}</td>
						</tr><tr>
							<td style="text-align: right;"><b>Carga Horária Preenchida:</b></td>
							<td style="text-align: right;">${moduloIMDMBean.chTotal}</td>
						</tr><tr>
							<td style="text-align: right;"><b>Carga Horária Restante:</b></td>
							<c:set var="total" value="${moduloIMDMBean.obj.cargaHoraria - moduloIMDMBean.chTotal}"/>
							<c:choose>
								<c:when test="${total < 0}">
									<td style="text-align: right; color: red;">${total}</td>
								</c:when>
								<c:otherwise>
									<td style="text-align: right;">${total}</td>		
								</c:otherwise>
							</c:choose>
							
						</tr>
			</c:when>
			<c:otherwise>
				<tr>
					<td colspan="4" style="text-align: center;"><font color="red">Nenhuma disciplina cadastrada</font> </td>
				</tr>
			</c:otherwise>
		</c:choose>
			  <tfoot>
			   <tr>
					<td colspan="3">
						<c:if test="${ moduloIMDMBean.obj.id > 0 }">
							<h:commandButton value="<< Voltar" action="#{moduloIMDMBean.listar}" id="voltar"/>
						</c:if>
						<h:commandButton value="#{moduloIMDMBean.confirmButton}" action="#{moduloIMDMBean.cadastrar}" id="cadastrar" />
						<h:commandButton value="Cancelar" action="#{moduloIMDMBean.cancelar}" onclick="#{confirm}" id="cancelar" />
					</td>
			   </tr>
			</tfoot>
		</table>
	
	<br />
	<center>
		<h:graphicImage url="/img/required.gif" style="vertical-align: top;" />
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	</center>
	
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>