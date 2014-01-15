<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > Validação das Inscrições do Vestibular em Lote</h2>

	<div class="descricaoOperacao">Caro usuário, confirme a lista de inscrições a serem validadas.</div>
	<h:form id="form">
		<a4j:keepAlive beanName="validacaoCandidatoBean"></a4j:keepAlive>
		<table class="formulario" width="80%">
			<caption>Dados da Validação</caption>
			<tr>
				<th class="rotulo" width="35%">Processo Seletivo:</th>
				<td>
					<h:outputText value="#{validacaoCandidatoBean.obj.processoSeletivo.nome}" />
				</td>
			</tr>
			<tr>
				<th class="rotulo" valign="top">Observações:</th>
				<td>
					<h:outputText value="#{ validacaoCandidatoBean.obj.observacao }" id="observacoes" />
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<table class="listagem" width="100%">
						<tbody>
							<c:set var="grupo" value="" />
							<c:forEach items="#{validacaoCandidatoBean.inscricoes}" var="item" varStatus="status">
								<c:if test="${ status.index == 0 || grupo != item.validada }" >
									<tr><td colspan="5" class="subFormulario">
										<h:outputText value="Inscrições Validadas Anteriormente" rendered="#{ item.validada }"/>
										<h:outputText value="Lista de Inscrições a Validar" rendered="#{ !item.validada }"/>
									</td></tr>
									<tr class="caixaCinza" style="font-weight: bold;">
										<th style="font-weight: bold;text-align: right;" width="5%">Ordem</th>
										<th style="font-weight: bold;text-align: center;" width="10%">Inscrição</th>
										<th style="font-weight: bold;text-align: right;" width="15%">CPF</th>
										<th style="font-weight: bold;text-align: left;">Nome</th>
										<th style="font-weight: bold;text-align: left;" width="15%">Status</th>
									</tr>
									<c:set var="grupo" value="${item.validada}" />
								</c:if>
								<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
									<td style="text-align: right;">${status.index + 1}</td>
									<td style="text-align: center;"><h:outputText value="#{item.numeroInscricao}"/></td>
									<td style="text-align: right;"><h:outputText value="#{item.pessoa.cpf_cnpjString}"/></td>
									<td style="text-align: left;">
										<h:outputText value="#{item.pessoa.nome}" />
									</td>
									<td>
										<h:outputText value="Validada" rendered="#{item.validada}"/>
										<h:outputText value="Pendente" rendered="#{not item.validada}"/>
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Cadastrar" action="#{validacaoCandidatoBean.cadastrar}" id="validaCPFs"/>
						<h:commandButton value="<< Voltar" action="#{validacaoCandidatoBean.formularioCadastro}" id="voltar"/> 
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{validacaoCandidatoBean.cancelar}" id="cancela" />
					</td>
				</tr>
			</tfoot>
		</table>
		<br>
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>