<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<f:view>
	<h2>Comprovante de Inscri��o em Processo de Sele��o para Fiscais
	do Vestibular</h2>

	<h3>Inscri��o No. ${inscricao.numeroInscricao}</h3>
	<br />
	<p>A inscri��o de ${inscricao.pessoa.nome}, CPF <ufrn:format
		type="cpf_cnpj" name="inscricao" property="pessoa.cpf_cnpj" />, <c:if
		test="${not empty inscricao.discente}">
		matr�cula ${inscricao.discente.matricula},
		</c:if> <c:if test="${not empty inscricao.servidor}">
		SIAPE ${inscricao.servidor.siape},
		</c:if> foi realizada com sucesso para o Processo de Sele��o para Fiscais do
	${inscricao.processoSeletivoVestibular.nome}.</p>
	<br />
	<c:if test="${not empty inscricao.localAplicacaoProvas}">
		<h4>Voc� optou em trabalhar preferencialmente nos seguintes
		locais:</h4>
		<ol>
			<c:forEach var="item" items="${inscricao.localAplicacaoProvas}"
				varStatus="count">
				<li>${item.nome}</li>
			</c:forEach>
		</ol>
	</c:if>

	<c:if test="${inscricao.disponibilidadeOutrasCidades}">
		<p>Voc� informou que tem disponibilidade para viajar para outros
		munic�pios para aplicar provas al�m de Natal.</p>
		<br />
	</c:if>
	<p>Lembramos que o fiscal ser� alocado para trabalhar nos locais de
	aplica��o de provas de acordo com a necessidade da COMPERVE.</p>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>