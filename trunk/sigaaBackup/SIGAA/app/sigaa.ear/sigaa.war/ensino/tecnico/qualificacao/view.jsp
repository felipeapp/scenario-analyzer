<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<a4j:keepAlive beanName="qualificacaoTecnicoMBean"/>
<h2> <ufrn:subSistema /> > Visualização </h2>
<h:form id="form">
	<table class="formulario" style="width: 50%">
	  <caption>Dados da Qualificação</caption>
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
				<th><b>Módulos:</b></th>
				<td>
					<c:forEach var="linha" items="#{qualificacaoTecnicoMBean.modulosAdicionados}" varStatus="status">
						<tr>
							<td></td>
							<td>${linha.codigo} - ${linha.descricao} - ${linha.cargaHoraria}</td>
						</tr>
					</c:forEach>
				</td>
			</tr>
		  <tfoot>
			   <tr>
					<td colspan="3">
						<c:choose>
							<c:when test="${qualificacaoTecnicoMBean.visualizar}">
								<h:commandButton value="#{qualificacaoTecnicoMBean.confirmButton}" action="#{qualificacaoTecnicoMBean.cadastrar}" id="btCadastrar" />
								<h:commandButton value="<< Dados Básicos" action="#{qualificacaoTecnicoMBean.dadosBasicos}" id="dadosBasicos" />
								<h:commandButton value="Cancelar" action="#{qualificacaoTecnicoMBean.cancelar}" onclick="#{confirm}" id="cancelar" />
							</c:when>
							<c:otherwise>
								<h:commandButton value="<< Voltar" action="#{qualificacaoTecnicoMBean.listar}" />
							</c:otherwise>
						</c:choose>
					</td>
			   </tr>
			</tfoot>
  </table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>