<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<f:view>
	<h2>Comprovante de Inscrição em Processo de Seleção para Fiscais
	do Vestibular</h2>

	<h3>Inscrição No. ${inscricao.numeroInscricao}</h3>
	<br />
	<p>A inscrição de ${inscricao.pessoa.nome}, CPF <ufrn:format
		type="cpf_cnpj" name="inscricao" property="pessoa.cpf_cnpj" />, <c:if
		test="${not empty inscricao.discente}">
		matrícula ${inscricao.discente.matricula},
		</c:if> <c:if test="${not empty inscricao.servidor}">
		SIAPE ${inscricao.servidor.siape},
		</c:if> foi realizada com sucesso para o Processo de Seleção para Fiscais do
	${inscricao.processoSeletivoVestibular.nome}.</p>
	<br />
	<c:if test="${not empty inscricao.localAplicacaoProvas}">
		<h4>Você optou em trabalhar preferencialmente nos seguintes
		locais:</h4>
		<ol>
			<c:forEach var="item" items="${inscricao.localAplicacaoProvas}"
				varStatus="count">
				<li>${item.nome}</li>
			</c:forEach>
		</ol>
	</c:if>

	<c:if test="${inscricao.disponibilidadeOutrasCidades}">
		<p>Você informou que tem disponibilidade para viajar para outros
		municípios para aplicar provas além de Natal.</p>
		<br />
	</c:if>
	<p>Lembramos que o fiscal será alocado para trabalhar nos locais de
	aplicação de provas de acordo com a necessidade da COMPERVE.</p>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>