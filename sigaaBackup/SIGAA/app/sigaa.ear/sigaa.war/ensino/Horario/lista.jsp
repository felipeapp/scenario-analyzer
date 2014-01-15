<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h:outputText value="#{horario.create}" />
<h:form>
<a4j:keepAlive beanName="cursoTecnicoMBean"/>
<h2> <ufrn:subSistema /> > Listagem dos Horários</h2>
	<c:if test="${!horario.unidadeNivelPreDefinidos}">
		<table class="formulario" width="75%">
			<caption>Escolha a gestora acadêmica e o nível de ensino</caption>
			<tr>
				<th width="25%" class="required" >Unidade Responsável:</th>
				<c:choose>
					<c:when test="${horario.unidadeNivelPreDefinidos}">
						<td colspan="8"><h:outputText value="#{horario.obj.unidade.nome}"/></td>
					</c:when>
					<c:otherwise>
						<td colspan="10"><h:selectOneMenu id="unidades" value="#{horario.obj.unidade.id}">
							<f:selectItems value="#{unidade.allGestorasAcademicasDefinemHorariosCombo}" />
						</h:selectOneMenu></td>
					</c:otherwise>
				</c:choose>
			</tr>
			<tr>
				<th  width="25%" class="required">Nível de Ensino:</th>
				<td colspan="10">
					<h:selectOneMenu value="#{horario.obj.nivel}" id="niveis">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{calendario.comboNiveis}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="11">
						<h:commandButton value="Buscar" action="#{horario.carregarHorarios}"/>
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{horario.cancelar}" id="cancelar"/>
					</td>
				</tr>
			</tfoot>
		</table>
		<center>
		 <h:graphicImage url="/img/required.gif" style="vertical-align: top;"/>
		 <span class="fontePequena"> Campos de preenchimento obrigatório. </span>
		</center>
	</c:if>
	<br/>
 
	<div class="infoAltRem">
		<h:commandLink action="#{horario.iniciar}" >
			<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;" title="Cadastrar"/>: Cadastrar
		</h:commandLink>
		<html:img page="/img/alterar.gif" style="overflow: visible;"/>: Atualizar
		<html:img page="/img/delete.gif" style="overflow: visible;"/>: Remover
	</div>
	
	<table class="formulario" width="50%">
	  <caption>Horários cadastrados</caption>
		<thead>
			<tr>
				<th style="text-align: center;">Hora Início</th>
				<th style="text-align: center;">Hora Fim</th>
				<th style="text-align: left;">Turno</th>
				<th style="text-align: center;">Ordem</th>
				<th></th>
				<th></th>
			</tr>
		</thead>
	<c:choose>
		<c:when test="${not empty horario.horariosDaUnidade}">	
			   <c:forEach var="linha" items="#{horario.horariosDaUnidade}" varStatus="status">
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
						<td style="text-align: center;"><ufrn:format type="hora" valor="${linha.inicio}" /> </td>
						<td style="text-align: center;"><ufrn:format type="hora" valor="${linha.fim}" /> </td>
						<td style="text-align: left;">${linha.turno}</td>
						<td style="text-align: center;">${linha.ordemFormatado}</td>
						<td width="2%" align="right">
							<h:commandLink action="#{horario.iniciarAtualizar}" >
								<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" title="Atualizar" />  
								<f:param name="id" value="#{linha.id}"/> 
							</h:commandLink>
						</td>
						<td width="2%" align="right">
							<h:commandLink action="#{horario.remover}" onclick="#{confirmDelete}">
								<h:graphicImage value="/img/delete.gif" style="overflow: visible;" title="Remover"/>
								<f:param name="id" value="#{linha.id}"/>
							</h:commandLink>
						</td>
					</tr>
			   </c:forEach>
		</c:when>
		<c:otherwise>
				 	<tr>
				 		<td colspan="5" align="center">Nenhum horário cadastrado.</td>
				 	</tr>			
		</c:otherwise>
	</c:choose>
	</table>
	
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>