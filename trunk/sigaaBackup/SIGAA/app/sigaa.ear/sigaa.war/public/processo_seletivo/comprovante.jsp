<%@include file="/public/include/cabecalho.jsp" %>

<style>
	.descricaoOperacao{
		font-size: 1.2em;
	}

	h3, h4 {
		font-variant: small-caps;
		text-align: center;
		margin: 2px 0 20px;
	}	
	
	h4 { margin: 15px 0 20px; }
	
	.descricaoOperacao p { text-align: justify; } 
	
	.codVer{text-align: center;display: block;position: relative;width: 100%;}
		.maiuscula{text-transform: capitalize;}	
</style>

<f:view>
	<a4j:keepAlive beanName="inscricaoSelecao"></a4j:keepAlive>
	<h:form id="formComprovanteInscricao">
	<h2>Inscrição em Processo Seletivo > Comprovante</h2>
	
	<div class="descricaoOperacao">
		<h3> Inscrição No. ${inscricaoSelecao.obj.numeroInscricao} </h3>
	
		<p>
				A inscrição de <b class="maiuscula">${fn:toLowerCase(inscricaoSelecao.obj.pessoaInscricao.nome)}</b>,
		<c:choose>
			<c:when test="${inscricaoSelecao.obj.pessoaInscricao.estrangeiro}">
				PASSAPORTE  <b>${inscricaoSelecao.obj.pessoaInscricao.passaporte}</b>
			</c:when>
			<c:otherwise>
				CPF  <b><ufrn:format type="cpf_cnpj" name="inscricaoSelecao" property="obj.pessoaInscricao.cpf"/></b>
			</c:otherwise>
		</c:choose>
		, foi submetida com sucesso para o <b class="maiuscula">${fn:toLowerCase(inscricaoSelecao.obj.processoSeletivo.editalProcessoSeletivo.nome)}</b>,
		no curso de									
		
		<c:choose>
			<%-- SE PROCESSO SELETIVO CURSO LATOS, PÓS E TÉCNICO --%>
			<c:when test="${not empty inscricaoSelecao.obj.processoSeletivo.curso}">
				<b class="maiuscula">${fn:toLowerCase(inscricaoSelecao.obj.processoSeletivo.curso.descricao)}</b> - 
				nível <b class="maiuscula">${fn:toLowerCase(inscricaoSelecao.obj.processoSeletivo.curso.nivelDescricao)}</b>, em
			</c:when>
			<%-- SE PROCESSO DE TRANSFERÊNCIA VOLUNTÁRIA--%>
			<c:otherwise>
				${fn:toLowerCase(inscricaoSelecao.obj.processoSeletivo.matrizCurricular.curso.nivelDescricao)} de
				<b class="maiuscula">${fn:toLowerCase(inscricaoSelecao.obj.processoSeletivo.matrizCurricular.curso.descricao)}</b>, 
				modalidade <b class="maiuscula">${fn:toLowerCase(inscricaoSelecao.obj.processoSeletivo.matrizCurricular.grauAcademico.descricao)}</b>,
				turno <b class="maiuscula">${fn:toLowerCase(inscricaoSelecao.obj.processoSeletivo.matrizCurricular.turno.descricao)}</b>,
				 no município de <b class="maiuscula">${fn:toLowerCase(inscricaoSelecao.obj.processoSeletivo.matrizCurricular.curso.municipio.nome)}</b>,  em
			</c:otherwise>
		</c:choose>
		
		<i><ufrn:format type="dataHora" name="inscricaoSelecao" property="obj.dataInscricao"/></i>.
		
		</p>
		
		<c:if test="${not empty inscricaoSelecao.obj.observacoes}">
			<h4 style="font-size: 0.9em; color: #555;"> Observações do Candidato </h4>
			<p>
				<ufrn:format type="texto" valor="${inscricaoSelecao.obj.observacoes}" />
			</p>
		</c:if>

		<c:set var="orientacoes" value="${inscricaoSelecao.obj.processoSeletivo.editalProcessoSeletivo.orientacoesInscritos}" />
		<c:if test="${not empty orientacoes}">
			<h4> Orientações Importantes</h4>
			${orientacoes}
		</c:if>
		
		<p>
			<center>
				<h:commandLink action="#{inscricaoSelecao.imprimirRequerimento}" rendered="#{not empty inscricaoSelecao.obj.processoSeletivo.matrizCurricular}" style="border: 0;" id="imprimirRequerimento" styleClass="naoImprimir">
					<h:graphicImage url="/img/imprimir.gif" alt="Imprimir Formulário Requerimento de Inscrição"/>
					<f:param name="id" value="#{inscricaoSelecao.obj.id}"/>
					Imprimir Formulário de Requerimento de Inscrição.
				</h:commandLink>
				<h:commandLink action="#{inscricaoSelecao.imprimirComprovante}" rendered="#{empty inscricaoSelecao.obj.processoSeletivo.matrizCurricular}" style="border: 0;" id="imprimirComprovante" styleClass="naoImprimir">
					<h:graphicImage url="/img/imprimir.gif" alt="Imprimir este Comprovante de Inscrição."/>
					<f:param name="id" value="#{inscricaoSelecao.obj.id}"/>
					Imprimir o Comprovante de Inscrição.
				</h:commandLink>
				<br clear="all"/>
				<h:commandLink action="#{inscricaoSelecao.imprimirGRU}" rendered="#{inscricaoSelecao.obj.processoSeletivo.editalProcessoSeletivo.taxaInscricao > 0}" style="border: 0;" id="imprimirGRU" styleClass="naoImprimir">
					<h:graphicImage url="/img/imprimir.gif" alt="Imprimir a GRU."/>
					<f:param name="id" value="#{inscricaoSelecao.obj.id}"/>
					Imprimir a Guia de Recolhimento da União (GRU) para pagamento da taxa de inscrição.
				</h:commandLink>
			</center>
		</p>
		
		<c:if test="${not empty inscricaoSelecao.obj.processoSeletivo.matrizCurricular && inscricaoSelecao.obj.processoSeletivo.possuiAgendamento}">
		<p>
			<b>O inscrito deverá comparecer no município  de 
			${inscricaoSelecao.obj.processoSeletivo.matrizCurricular.curso.municipio.nome}, data 	
			<ufrn:format type="data" valor="${inscricaoSelecao.obj.agenda.dataAgenda}" />
			, para entrega dos documentos, conforme descrito no edital.</b> 
		</p>
		</c:if>

		
		<br clear="all">
		<p>
			<small  class="codVer">
			Código Verificador: ${inscricaoSelecao.obj.codigoHash}
			</small>
		</p>

	</div>

	
	<c:if test="${not empty visualizacao}">
		<br />
		<div class="voltar" style="text-align: center;">
			<a href="javascript: history.go(-1);"> << Voltar </a>
		</div>	
	</c:if>
	
		<c:if test="${empty visualizacao}">
		<div class="voltar" style="text-align: center;">
			<%@include file="/public/include/voltar.jsp" %>
		</div>	
	</c:if>
	</h:form>
</f:view>

<%@include file="/public/include/rodape.jsp" %>