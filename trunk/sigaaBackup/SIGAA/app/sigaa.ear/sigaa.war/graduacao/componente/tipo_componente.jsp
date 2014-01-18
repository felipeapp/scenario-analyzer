<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.ensino.dominio.FormaParticipacaoAtividade"%>

<f:view>
	<%@include file="/stricto/menu_coordenador.jsp"%>
	<h2 class="title"><ufrn:subSistema /> &gt; Cadastro de Componente Curricular &gt; Tipo do Componente Curricular</h2>
	
	<div class="descricaoOperacao">
		<p>Caro usuário,</p><br>
		<p>apenas acessos com perfil de Administrador Acadêmico podem mudar o tipo de componente, 
		apenas para os componentes não oferecidos em turmas.</p>
	</div>
	
	<h:form id="form">
		<br>
		<table class="formulario" width="55%">
			<caption class="formulario">Selecione o tipo do Componente Curricular</caption>
			<tr>
				<th class="obrigatorio">Tipo do Componente:</th>
				<td>
					<c:if test="${componenteCurricular.escolheTipoComponente}">
						<a4j:region>
							<h:selectOneMenu
								value="#{componenteCurricular.obj.tipoComponente.id}" id="tipo" immediate="true"
								disabled="#{componenteCurricular.naoPermiteAlterarTipoComponente}" >
								<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
								<f:selectItems value="#{componenteCurricular.tiposComponentes}" />
								<a4j:support event="onchange" reRender="form" />
							</h:selectOneMenu>
						</a4j:region> 
					</c:if>
					<c:if test="${not componenteCurricular.escolheTipoComponente}">
						<b>${componenteCurricular.obj.tipoComponente.descricao}</b>
					</c:if>
				</td>
			</tr>
			<c:if test="${componenteCurricular.obj.passivelTipoAtividade}">
				<tr>
					<th class="obrigatorio">Tipo de ${componenteCurricular.obj.atividade ? 'Atividade' : 'Disciplina'}:</th>
					<td>
						<a4j:region>
							<h:selectOneMenu
								value="#{componenteCurricular.obj.tipoAtividade.id}" immediate="true"
								id="tipoAtividade" disabled="#{componenteCurricular.naoPermiteAlterarTipoAtividade}"
								valueChangeListener="#{componenteCurricular.carregarFormaParticipacao}">
								
								<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
								<c:if test="${componenteCurricular.obj.passivelTipoAtividade && !componenteCurricular.obj.atividade}">
									<f:selectItem itemValue="-1" itemLabel="REGULAR" />
								</c:if>	
								<f:selectItems value="#{tipoAtividade.allCombo}" />
								<a4j:support event="onchange" reRender="form, labelFormaParticipacao, formaParticipacao" />
							</h:selectOneMenu>
						</a4j:region>
					</td>
				</tr>
				<tr>
					<th> <h:outputLabel id="labelFormaParticipacao" value="Forma de Participação:" styleClass="required" rendered="#{componenteCurricular.obj.tipoAtividade.id > 0 || componenteCurricular.obj.atividade}" /> </th>
					<td>
						<h:selectOneMenu value="#{componenteCurricular.obj.formaParticipacao.id}" id="formaParticipacao" immediate="true"
							disabled="#{componenteCurricular.naoPermiteAlterarFormaParticipacao}"
							rendered="#{componenteCurricular.obj.tipoAtividade.id > 0 || componenteCurricular.obj.atividade}">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{componenteCurricular.formaParticipacaoCombo}" />
						</h:selectOneMenu> 
					</td>
				</tr>
			</c:if>
			<tr>
				<th class="obrigatorio">Modalidade de Educação:</th>
				<td>
					<h:selectOneMenu
						value="#{componenteCurricular.obj.modalidadeEducacao.id}" id="modalidadeEducacao" >
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
						<f:selectItems value="#{componenteCurricular.allModalidades}" />
					</h:selectOneMenu>
				</td>
			</tr>	
			<tfoot>
				<tr>
					<td colspan="2">
						<c:if test="${componenteCurricular.obj.id != 0 and (acesso.lato || acesso.complexoHospitalar)}">
							<h:commandButton id="voltar" value="<< Voltar" action="#{componenteCurricular.formBusca}" />
						</c:if>
						<h:commandButton id="cancelar" value="Cancelar" onclick="#{confirm}" action="#{componenteCurricular.cancelar}" immediate="true" />
						<h:commandButton id="avancar" value="Avançar >>" action="#{componenteCurricular.submeterTipoComponente}" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br>
	<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	<br>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
