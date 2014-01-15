<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > Processos Seletivos > Valida��o de Pagamento da Taxa de Inscri��o</h2>
	
	<div class="descricaoOperacao">Este formul�rio permite validar as inscri��es dos candidatos que efeturam o pagamento da taxa de inscri��o.<br/>
		A lista abaixo apresenta as inscri��es que foram pagas e n�o est�o
		validadas. Por favor, verifique a lista e confirme a valida��o do
		pagamento para concluir a opere��o.
	</div>
	
	<h:form id="formListaInscritos">
		<a4j:keepAlive beanName="validaInscricaoSelecaoMBean"></a4j:keepAlive>
		<table class="formulario" width="80%">
			<caption>Dados do Processo Seletivo</caption>
			<tbody>
				<tr>
					<th class="rotulo">Edital:</th>
					<td>${validaInscricaoSelecaoMBean.obj.editalProcessoSeletivo.nome}</td>
				</tr>
				<tr>
					<th class="rotulo">Per�odo de Inscri��es:</th>
					<td><ufrn:format type="data"
						valor="${validaInscricaoSelecaoMBean.obj.editalProcessoSeletivo.inicioInscricoes}"></ufrn:format> a <ufrn:format
						type="data" valor="${validaInscricaoSelecaoMBean.obj.editalProcessoSeletivo.fimInscricoes}"></ufrn:format>
					</td>
				</tr>
				<tr>
					<th class="rotulo">N�mero de Inscritos:</th>
					<td>${validaInscricaoSelecaoMBean.obj.editalProcessoSeletivo.qtdInscritos}</td>
				</tr>
				<tr>
					<td colspan="2" class="subFormulario">Lista de Inscri��es n�o validadas com GRUs pagas.</td>
				</tr>
				<tr>
					<td colspan="2">
						<table class="listagem" width="100%">
							<thead>
								<tr>
									<th style="text-align: right;" width="10%">Inscricao</th>
									<th style="text-align: left;">Nome</th>
									<th style="text-align: center;" width="15%">CPF</th>
								</tr>
							</thead>
							<c:forEach var="inscrito" items="#{ validaInscricaoSelecaoMBean.inscricoesPagas }" varStatus="status">
								<tr>
									<td style="text-align: right;"> ${ inscrito.numeroInscricao } </td>
									<td style="text-align: left;"> ${ inscrito.pessoaInscricao.nome } </td>
									<td style="text-align: center;"> <ufrn:format type="cpf_cnpj" valor="${ inscritos.pessoaInscricao.cpf_cnpj }"/> </td>
								</tr>
							</c:forEach>
						</table>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Verificar Pagamentos" action="#{validaInscricaoSelecaoMBean.validar}" id="validar"/> 
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{validaInscricaoSelecaoMBean.cancelar}" id="cancela" />
					</td>
				</tr>
			</tfoot>
		</table>

		<br/>
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
		<br/>
		<c:if test="${not empty validaInscricaoSelecaoMBean.inscricoes or not empty validaInscricaoSelecaoMBean.inscricoesDuplicadas}">
			<div class="descricaoOperacao">As seguintes inscri��es ser�o validadas. Verifique a lista abaixo e confirme a valida��o.
				<c:if test="${not empty validaInscricaoSelecaoMBean.inscricoesDuplicadas}">
					<br/><b>H� inscri��es em duplicidade (mais de uma inscri��o paga para o mesmo CPF ou Passaporte)</b>: verifique a data de pagamento das inscri��es em duplicidade e marque apenas a que ser� validada.
				</c:if>
			</div>
			<table class="listagem">
				<caption class="listagem">Lista de Inscri��es a Validar (${fn:length(validaInscricaoSelecaoMBean.inscricoes)})</caption>
				<thead>
					<tr>
						<th width="3%">Validar</th>
						<th width="8%" style="text-align: right;">Inscri��o</th>
						<th width="10%" style="text-align: right;">N�m. Ref. GRU</th>
						<th width="30%">Nome</th>
						<th width="12%" style="text-align: center;">CPF ou Passaporte</th>
						<th>Status</th>
						<th>Observa��o</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="#{validaInscricaoSelecaoMBean.inscricoes}" var="item" varStatus="status">
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
							<td style="text-align: center;"><h:selectBooleanCheckbox value="#{item.selecionado}" id="selecionado"/></td>
							<td style="text-align: right;">${item.numeroInscricao}</td>
							<td style="text-align: right;">${item.numeroReferenciaGRU}</td>
							<td>${item.pessoaInscricao.nome}</td>
							<td style="text-align: right;">
								<c:choose>
									<c:when test="${not empty item.pessoaInscricao.cpf and item.pessoaInscricao.cpf > 0}">
										<ufrn:format type="cpf_cnpj" valor="${item.pessoaInscricao.cpf}" />
									</c:when>
									<c:when test="${empty item.pessoaInscricao.cpf or item.pessoaInscricao.cpf == 0}">
										${item.pessoaInscricao.passaporte}
									</c:when>
								</c:choose>
							</td>
							<td style="text-align: left !important;">${item.descricaoStatus}</td>
							<td><h:inputTextarea value="#{item.observacoes}"  cols="35" rows="2" id="observacoes"/></td>
						</tr>
					</c:forEach>
					<c:if test="${not empty validaInscricaoSelecaoMBean.inscricoesDuplicadas}">
					<tr>
						<td colspan="7" class="subFormulario">Inscri��es em Duplicidade (mais de uma inscri��o paga para o mesmo CPF ou Passaporte)</td>
					</tr>
					<c:forEach items="#{validaInscricaoSelecaoMBean.inscricoesDuplicadas}" var="item" varStatus="status">
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
							<td style="text-align: center;"><h:selectBooleanCheckbox value="#{item.selecionado}" id="selecionadoDuplicada"/></td>
							<td style="text-align: right;">${item.numeroInscricao}</td>
							<td style="text-align: right;">${item.numeroReferenciaGRU}</td>
							<td>${item.pessoaInscricao.nome}</td>
							<td style="text-align: right;">
								<c:choose>
									<c:when test="${not empty item.pessoaInscricao.cpf and item.pessoaInscricao.cpf > 0}">
										<ufrn:format type="cpf_cnpj" valor="${item.pessoaInscricao.cpf}" />
									</c:when>
									<c:when test="${empty item.pessoaInscricao.cpf or item.pessoaInscricao.cpf == 0}">
										${item.pessoaInscricao.passaporte}
									</c:when>
								</c:choose>
							</td>
							<td style="text-align: left !important;">${item.descricaoStatus}</td>
							<td><h:inputTextarea value="#{item.observacoes}" cols="35" rows="2" id="observacoesDuplicada"/></td>
						</tr>
					</c:forEach>
					</c:if>
				</tbody>			
				<tfoot>
					<tr>
						<td colspan="10" align="center">
							<h:commandButton id="validar" value="Validar Inscri��es Selecionadas" action="#{validaInscricaoSelecaoMBean.validar}" />
							<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{validaInscricaoSelecaoMBean.cancelar}" id="cancela2" />
						</td>
					</tr>
				</tfoot>
			</table>
		</c:if>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>