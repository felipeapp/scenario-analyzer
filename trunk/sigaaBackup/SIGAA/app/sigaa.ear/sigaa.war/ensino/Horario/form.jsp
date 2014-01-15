<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:messages showDetail="true" /> 
	<h:outputText value="#{horario.create}" />
	<h2 class="title"><ufrn:subSistema /> > Horários das Turmas</h2>
	<h:form id="form">
		<div class="infoAltRem" style="text-align: center; width: 100%">
			<h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
  			<h:commandLink value="Listar" action="#{horario.listar}"/>
		</div>
		<table class="formulario" width="75%">
			<caption>Escolha a gestora acadêmica e o nível de ensino</caption>
			<tr>
				<th colspan="3" class="required">Unidade Responsável:</th>
				<c:choose>
					<c:when test="${horario.unidadeNivelPreDefinidos}">
						<td colspan="10"><h:outputText value="#{horario.obj.unidade.nome}"/></td>
					</c:when>
					<c:otherwise>
						<td colspan="10"><h:selectOneMenu  onchange="submit()" id="unidades" value="#{horario.obj.unidade.id}">
							<f:selectItems value="#{unidade.allGestorasAcademicasDefinemHorariosCombo}" />
						</h:selectOneMenu></td>
					</c:otherwise>
				</c:choose>
			</tr>
			<tr>
				<th colspan="3" class="required">Nível de Ensino:</th>
				<c:choose>
					<c:when test="${horario.unidadeNivelPreDefinidos}">
						<td colspan="10"><h:outputText value="#{horario.obj.descricaoNivel}"/></td>
					</c:when>
					<c:otherwise>
							<td colspan="10"><h:selectOneMenu onchange="submit()" value="#{horario.obj.nivel}"
								valueChangeListener="#{horario.carregarHorarios}" id="niveis">
								<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
								<f:selectItems value="#{calendario.comboNiveis}" />
							</h:selectOneMenu></td>
					</c:otherwise>
				</c:choose>
			</tr>
			<tr>
				<th colspan="3" class="required">Hora de Início:</th>
				<td width="30%">
					<h:inputText id="horaInicio" value="#{horario.obj.inicio}" size="5" maxlength="5"
						onkeypress="return(formataHora(this, event))">	
						<f:converter converterId="convertHora" />
					</h:inputText>
				</td>
				<th width="20%" class="required">Hora de Fim:</th>
				<td><h:inputText id="horaFim" value="#{horario.obj.fim}" size="5" maxlength="5"
						onkeypress="return(formataHora(this, event))">
						<f:converter converterId="convertHora" />
					</h:inputText>
				</td>
			</tr>
			<tr>
				<th colspan="3" class="required">Turno:</th>
				<td><h:selectOneMenu value="#{horario.obj.tipo}">
				<f:selectItem itemValue="0" itemLabel="--SELECIONE--"/>
				<f:selectItems value="#{horario.turnos}"/>
				</h:selectOneMenu> </td>
				
				<th class="required">Ordem:</th> 
				<td><h:inputText value="#{horario.obj.ordem}" onkeyup="return formatarInteiro(this);" onchange="return formatarInteiro(this);" maxlength="1" size="1"/></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="10">
					 <h:commandButton value="#{horario.confirmButton }" action="#{horario.cadastrarHorarios}" />
					 <h:commandButton value="Cancelar" onclick="#{confirm}" action="#{horario.cancelar}" id="cancelar" /> 
					</td>
				</tr>
			</tfoot>
		</table>
		<br>
		<c:if test="${not empty horario.horariosDaUnidade}">
		<table class="listagem" style="width: 50%">
			<caption>Horários Cadastrados para unidade e nível escolhidos</caption>
				<thead>
					<td style="text-align: center;">Hora de Início</td>
					<td style="text-align: center;">Hora de Fim</td>
					<td style="text-align: left;">Turno</td>
					<td style="text-align: right;">Ordem</td>
					<td width="5%"></td>
				</thead>
			<c:forEach items="#{horario.horariosDaUnidade}" var="h" varStatus="s">
				<tr>
					<td style="text-align: center;"><ufrn:format type="hora" valor="${h.inicio}" /> </td>
					<td style="text-align: center;"><ufrn:format type="hora" valor="${h.fim}" /> </td>
					<td style="text-align: left;">${h.turno}</td>
					<td style="text-align: right;">${h.ordem}</td>
					<td></td>
				</tr>
			</c:forEach>
		</table>
		</c:if>
	</h:form>
	
 <br />
 <center>
 <h:graphicImage url="/img/required.gif" style="vertical-align: top;"/>
 <span class="fontePequena"> Campos de preenchimento obrigatório. </span>
 </center>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>