<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > Definição de Locais de Provas dos Candidtos</h2>

	<div class="descricaoOperacao"><p><b>Caro usuário</b>,</p>
	<p>Verifique os dados para definição de local de prova dos candidatos antes de confirmar.</p>
	</div>
	<h:form id="form">
		<a4j:keepAlive beanName="importaLocalProvaCandidatoBean"></a4j:keepAlive>
		<table class="formulario" width="80%">
			<caption>Dados da Validação</caption>
			<tr>
				<th class="rotulo" width="35%">Processo Seletivo:</th>
				<td>
					<h:outputText value="#{importaLocalProvaCandidatoBean.obj.processoSeletivo.nome}" />
				</td>
			</tr>
			<tr>
				<td class="subFormulario" colspan="2">Definição de Locais de Prova dos Candidatos</td>
			</tr>
			<tr>
				<td colspan="2">
					<table class="listagem" width="100%">
						<thead>
							<tr>
								<th style="text-align: right;" width="8%">Inscrição</th>
								<th style="text-align: center;" width="15%">CPF</th>
								<th style="text-align: left;">Nome</th>
								<th style="text-align: right;" width="5%">Turma</th>
							</tr>
						</thead>
						<tbody>
							<c:set var="localAnterior" value="" />
							<c:forEach items="#{importaLocalProvaCandidatoBean.resultadosBusca}" var="item" varStatus="status">
								<c:if test="${localAnterior != item.localProva.nome}">
									<tr>
										<td colspan="4" class="subFormulario">${ item.localProva.nome }</td>
									</tr>
									<c:set var="localAnterior" value="${item.localProva.nome}"/>
								</c:if>
								<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
									<td style="text-align: right;"><h:outputText value="#{item.numeroInscricao}"/></td>
									<td style="text-align: center;"><h:outputText value="#{item.pessoa.cpf_cnpjString}"/></td>
									<td style="text-align: left;"><h:outputText value="#{item.pessoa.nome}" /></td>
									<td style="text-align: right;"><h:outputText value="#{item.turma}" /></td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Cadastrar" action="#{importaLocalProvaCandidatoBean.cadastrar}" id="cadastrar"/>
						<h:commandButton value="<< Voltar" action="#{importaLocalProvaCandidatoBean.formularioCadastro}" id="voltar"/> 
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{importaLocalProvaCandidatoBean.cancelar}" id="cancela" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>