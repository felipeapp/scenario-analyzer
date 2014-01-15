<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> &gt; Cadastro Avulso de Justificativa de Ausência</h2>
	
	<h:form>
		<a4j:keepAlive beanName="justificativaAusencia" />
		<table class="formulario" width="60%">
		<caption>Informe o Processo Seletivo</caption>
		<tbody>
			<tr>
				<th class="required">Processo Seletivo:</th>
				<td>
					<h:selectOneMenu value="#{justificativaAusencia.idProcessoSeletivo}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{processoSeletivoVestibular.allAtivoCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton value="Buscar" action="#{justificativaAusencia.listarFiscaisAusentes}" /> 
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{justificativaAusencia.cancelar}" immediate="true" />
				</td>
			</tr>
		</tfoot>
		</table>
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
		<br/>
		<c:if test="${ not empty justificativaAusencia.listaFiscais }">
		<div class="infoAltRem">
			<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Cadastrar Justificativa
		</div>
			<table class="listagem">
				<caption>Lista de Fiscais Ausentes</caption>
				<thead>
					<tr>
						<th>Perfil</th>
						<th>Matrícula/<br/>SIAPE</th>
						<th>Nome</th>
						<th>Curso/<br/>Unidade</th>
						<th>Ausente</th>
						<th></th>
					</tr>
				</thead>
				<c:forEach items="#{justificativaAusencia.listaFiscais}" var="item" varStatus="loop">
					<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<c:if test="${item.perfilDiscente}">
							<td>Discente</td>
							<td>${item.discente.matricula}</td>
							<td>${item.discente.nome}</td>
							<td>${item.discente.curso.descricaoCompleta}</td>
						</c:if>
						<c:if test="${item.perfilServidor}">
							<td>Servidor</td>
							<td>${item.servidor.siape}</td>
							<td>${item.servidor.nome}</td>
							<td>${item.servidor.unidade.nome}</td>
						</c:if>
						<td>
							<h:outputText value="Reunião" rendered="#{not item.presenteReuniao}"/>
							<h:outputText value="Aplicação" rendered="#{not item.presenteAplicacao}"/>
						</td>
						<td>
							<h:commandLink action="#{ justificativaAusencia.cadastrarJustificativaAvulsa }">
								<f:verbatim>
									<img src="/sigaa/img/seta.gif" alt="Cadastrar Justificativa" title="Cadastrar Justificativa" />
								</f:verbatim>
								<f:param name="id" value="#{ item.id }" />
							</h:commandLink>
						</td>
					</tr>
				</c:forEach>
			</table>
		</c:if>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
