<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<f:view>
	<h2>Resultado do Processo de Seleção para Fiscais do Vestibular</h2>

	<h3>Inscrição No. ${inscricaoSelecaoFiscalVestibular.obj.numeroInscricao}</h3>
	<br />
	<c:if test="${not empty inscricaoSelecaoFiscalVestibular.fiscal}">
		<p>Você foi selecionado para trabalhar como fiscal <c:if
			test="${inscricaoSelecaoFiscalVestibular.fiscal.reserva}">
			<b>reserva</b>.
		</c:if> <c:if test="${not inscricaoSelecaoFiscalVestibular.fiscal.reserva}">
			<b>titular</b>.
		</c:if></p>
		<br />
		<p>
			<c:if test="${empty inscricaoSelecaoFiscalVestibular.localProva}">
				O local de aplicação de prova onde você irá trabalhar e o local de
				reunião serão divulgados em breve.
				<br />
				<c:if test="${not empty inscricaoSelecaoFiscalVestibular.obj.localAplicacaoProvas}">
					<h4>Você optou em trabalhar preferencialmente nos seguintes
					locais:</h4>
					<ol>
						<c:forEach var="item" items="${inscricaoSelecaoFiscalVestibular.obj.localAplicacaoProvas}"
							varStatus="count">
							<li>${item.nome}</li>
						</c:forEach>
					</ol>
				</c:if>
			</c:if> 
			
			<c:if test="${not empty inscricaoSelecaoFiscalVestibular.localProva}">
				Local de aplicação de prova: ${inscricaoSelecaoFiscalVestibular.localProva.localAplicacaoProva.nome}<br />  <br/>
				Coordenador(es) de aplicação: ${inscricaoSelecaoFiscalVestibular.localProva.coordenador == null ? inscricaoSelecaoFiscalVestibular.localProva.nomeCoordenador : inscricaoSelecaoFiscalVestibular.localProva.coordenador.nome}<br /> <br />
				Reunião: 
				<b>${inscricaoSelecaoFiscalVestibular.localProva.localReuniao}</b>
				<br />
			</c:if>
		</p>
		<br />
		<c:if test="${inscricaoSelecaoFiscalVestibular.obj.disponibilidadeOutrasCidades}">
			<p>Você informou que tem disponibilidade para viajar para outros
			municípios para aplicar provas além de Natal.</p>
			<br />
		</c:if>
		<p>Lembramos que o fiscal é alocado para trabalhar nos locais de
		aplicação de provas de acordo com a necessidade da COMPERVE.</p>
	</c:if>
	<c:if test="${empty inscricaoSelecaoFiscalVestibular.fiscal}">
		<p>Você não foi selecionado para trabalhar como fiscal.<br/>
		<c:if test="${not empty inscricaoSelecaoFiscalVestibular.obj.observacao}">
			Motivo: ${inscricaoSelecaoFiscalVestibular.obj.observacao}
		</c:if>
		</p>
	</c:if>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>