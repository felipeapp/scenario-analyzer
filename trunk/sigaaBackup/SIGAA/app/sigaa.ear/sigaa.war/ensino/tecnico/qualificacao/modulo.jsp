<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<a4j:keepAlive beanName="qualificacaoTecnicoMBean"/>
<h2> <ufrn:subSistema /> > Dados Básicos > Módulos </h2>
<h:form id="form">

	<div class="infoAltRem">
		<html:img page="/img/adicionar.gif" style="overflow: visible;"/>: Adicionar
		<html:img page="/img/delete.gif" style="overflow: visible;"/>: Remover
	</div>


	<table class="formulario" style="width: 90%">
	  <caption>Dados Básicos da Qualificação</caption>
		<h:inputHidden value="#{qualificacaoTecnicoMBean.obj.id}" />
			<tr>
				<th width="25%"><b>Curso:</b></th>
				<td>
					<h:outputText value="#{qualificacaoTecnicoMBean.obj.cursoTecnico.nome}" id="nomeCurso"/>
				</td>
			</tr>
			<tr>
				<th><b>Descrição:</b></th>
				<td colspan="2"><h:outputText value="#{qualificacaoTecnicoMBean.obj.descricao}"/></td>
			</tr>
			<tr>
				<th><b>Habilitação:</b></th>
				<td>
					<h:outputText value="#{qualificacaoTecnicoMBean.obj.habilitacao == true ? 'Sim' : 'Não'}"/>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<table class="subFormulario" width="100%">
					 <caption>Indique quais o módulos são necessários para essa qualificação</caption>
						<tr>
							<th class="obrigatorio" width="25%">Módulo:</th>
							<td>
								<h:selectOneMenu value="#{qualificacaoTecnicoMBean.modulo.id}" id="modulo">
									<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
									<f:selectItems value="#{qualificacaoTecnicoMBean.allModulosCursoTecnico}" />
								</h:selectOneMenu>
								
								<h:commandLink action="#{qualificacaoTecnicoMBean.cadastrarModulo}" >
									<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;" title="Adicionar Módulo"/>
								</h:commandLink>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<c:if test="${not empty qualificacaoTecnicoMBean.modulosAdicionados}">
				<tr>
					<td colspan="3">
						<table class="subFormulario" width="100%">
							<caption>Módulos adicionados nesta qualificação</caption>
								<c:forEach var="linha" items="#{qualificacaoTecnicoMBean.modulosAdicionados}" varStatus="status">
									<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
										<td>${linha.codigo} - ${linha.descricao} - ${linha.cargaHoraria}</td>
										<td width="20">
											<h:commandLink action="#{qualificacaoTecnicoMBean.removerModulo}" >
												<h:graphicImage value="/img/delete.gif" style="overflow: visible;" title="Remover"/>
												<f:param name="id" value="#{linha.id}"/>
											</h:commandLink>
										</td>
									</tr>
								</c:forEach>
						</table>
					</td>
				</tr>
			</c:if>
		  <tfoot>
			   <tr>
					<td colspan="3">
						<h:commandButton value="<< Voltar" action="#{qualificacaoTecnicoMBean.dadosBasicos}" id="dadosBasicos" />
						<h:commandButton value="Cancelar" action="#{qualificacaoTecnicoMBean.cancelar}" onclick="#{confirm}" id="cancelar" />
						<h:commandButton value="avançar >>" action="#{qualificacaoTecnicoMBean.view}" id="cadastrar" />
					</td>
			   </tr>
			</tfoot>
  </table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>