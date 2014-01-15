<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > Cadastro de CPFs de Isentos da Taxa de Inscrição</h2>

	<div class="descricaoOperacao">Caro usuário, confirme os CPFs e demais dados informados para isenção da Taxa do Vestibular.</div>
	<h:form id="form">
		<a4j:keepAlive beanName="isencaoTaxaInscricao"></a4j:keepAlive>
		<table class="formulario" width="80%">
			<caption>Dados da Isenção</caption>
			<tr>
				<th class="rotulo" width="35%">Processo Seletivo:</th>
				<td>
					<h:outputText value="#{isencaoTaxaInscricao.obj.processoSeletivoVestibular.nome}" />
				</td>
			</tr>
			<tr>
				<th class="rotulo">Isenção Total da Taxa de Inscrição:</th>
				<td>
					<ufrn:format type="simnao" valor="${isencaoTaxaInscricao.obj.isentoTotal}" />
				</td>
			</tr>
			<c:if test="${not isencaoTaxaInscricao.obj.isentoTotal}">
				<tr>
					<th class="rotulo">Valor da Taxa de Inscrição a Pagar:</th>
					<td>
						<ufrn:format type="moeda" valor="${isencaoTaxaInscricao.obj.valor}" />
					</td>
				</tr>
			</c:if>
			<tr>
				<th class="rotulo" valign="top">Tipo de Isento:</th>
				<td>
					<h:outputText value="Estudante" rendered="#{isencaoTaxaInscricao.obj.estudante}"/>
					<h:outputText value="Funcionário" rendered="#{isencaoTaxaInscricao.obj.funcionario}"/>
				</td>
			</tr>
			<tr>
				<th class="rotulo" valign="top">Observações:</th>
				<td>
					<h:outputText value="#{ isencaoTaxaInscricao.obj.observacao }" id="observacoes" />
				</td>
			</tr>
			<tr>
				<td class="subFormulario" colspan="2">Lista de CPF</td>
			</tr>
			<tr>
				<td colspan="2">
					<table class="listagem" width="100%">
						<thead>
							<tr>
								<th style="text-align: right;">Ordem</th>
								<th style="text-align: center;">CPF</th>
								<th style="text-align: left;">Nome</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="#{isencaoTaxaInscricao.isentos}" var="item" varStatus="status">
								<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
									<td style="text-align: right;">${status.index + 1}</td>
									<td style="text-align: center;"><h:outputText value="#{item.cpfFormatado}"/></td>
									<td style="text-align: left;">
										<h:outputText value="#{item.pessoa.nome}" />
										<h:outputText value="Não há cadastro deste CPF no módulo Vestibular" rendered="#{empty item.pessoa}" />
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
						<h:commandButton value="Cadastrar" action="#{isencaoTaxaInscricao.cadastrar}" id="validaCPFs"/>
						<h:commandButton value="<< Voltar" action="#{isencaoTaxaInscricao.formularioCadastro}" id="voltar"/> 
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{isencaoTaxaInscricao.cancelar}" id="cancela" />
					</td>
				</tr>
			</tfoot>
		</table>
		<br>
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>