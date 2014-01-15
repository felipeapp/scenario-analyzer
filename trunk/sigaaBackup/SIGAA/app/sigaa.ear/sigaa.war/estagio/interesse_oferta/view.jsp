<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<a4j:keepAlive beanName="ofertaEstagioMBean" />
<h2> <ufrn:subSistema /> &gt; Consultar Ofertas de Estágio &gt; Visualizar Inscrições</h2>

<div class="descricaoOperacao">
	<p><b>Caro Usuário,</b></p>
	<p>Nesta tela serão exibidos todos os discentes Inscritos na <b>Oferta de Estágio</b> selecionada. 
	Para cada discente é possível visualizar seu currículo. 
	</p>
	
	<c:if test="${interesseOfertaMBean.portalConcedenteEstagio}">
		 <p>Também é possível <b>Selecionar Estagiário</b>.
		 Uma vez selecionado para uma das vagas da <b>Oferta de Estágio</b>, 		 
		 é necessário que a <b>Coordenação do seu curso Efetive o seu cadastro</b> como estagiário
		 através do Portal do Coordenador de Curso de Graduação, assim o estágio será validado. 
		 </p>
	</c:if>
</div>

<table class="visualizacao" style="width: 90%">
	<caption>Dados da Oferta de Estágio</caption>
	<tr>
		<th style="width: 30%;">Título:</th>
		<td colspan="3">
			<h:outputText value="#{interesseOfertaMBean.ofertaEstagio.titulo}"/>
		</td>
	</tr>
	<tr>
		<th>Número de Vagas:</th>
		<td style="width: 15%;">
			<h:outputText value="#{interesseOfertaMBean.ofertaEstagio.numeroVagas}"/> 				
		</td>
		<th style="width: 20%;">Valor da Bolsa:</th>		
		<td>
			<ufrn:format type="moeda" valor="${interesseOfertaMBean.ofertaEstagio.valorBolsa}"></ufrn:format>				
		</td>	
	</tr>
	<tr>
		<th>Início da Publicação:</th>
		<td>
			<ufrn:format type="data" valor="${interesseOfertaMBean.ofertaEstagio.dataInicioPublicacao}"></ufrn:format>
		</td>
		<th>Fim da Publicação:</th>
		<td>
			<ufrn:format type="data" valor="${interesseOfertaMBean.ofertaEstagio.dataFimPublicacao}"></ufrn:format> 							
		</td>
	</tr>		
	<tr>
		<td colspan="4" class="subFormulario">Descrição da Oferta</td>
	</tr>	
	<tr>
		<td colspan="4">
			${interesseOfertaMBean.ofertaEstagio.descricao}
		</td>
	</tr>		
</table>
<br/>

<center>
	<div class="infoAltRem">
		<h:form>
			<h:graphicImage  value="/img/report.png"/>: Visualizar Currículo			
			<h:graphicImage  value="/img/prodocente/lattes.gif"/>: Visualizar Currículo Lattes
			<c:if test="${interesseOfertaMBean.permiteSelecionaDiscente}">
				<h:graphicImage value="/img/estagio/novo_estagiario.png" style="overflow: visible;"/>: Selecionar Estagiário
			</c:if>
		</h:form>
	</div>
</center>

<h:form id="form">
	<table class="listagem" style="width: 90%">
		<caption class="listagem">Lista de Inscritos (${fn:length(interesseOfertaMBean.listaInteresses)})</caption>
		<thead>
			<tr>
				<th style="text-align: center; width: 120px;">Matrícula</th>
				<th style="text-align: left; width: 500px;">Nome</th>
				<th>Situação</th>
				<th colspan="4"></th>
			</tr>
		</thead>
		<c:set var="idCurso" value="0"/> 
		<c:forEach items="#{interesseOfertaMBean.listaInteresses}" var="interesse" varStatus="loop">	
			<c:if test="${idCurso != interesse.discente.curso.id}">
				<tr>
					<td colspan="6" class="subFormulario">${interesse.discente.curso.descricao}</td>
				</tr>
				<c:set var="idCurso" value="${interesse.discente.curso.id}"/>		
			</c:if>
			
			<tr class="${loop.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td style="text-align: center;">${interesse.discente.matricula}</td>
				<td style="text-align: left;">${interesse.discente.nome}</td>
				<td style="text-align: left;">
					<c:if test="${interesse.selecionado}">
						<span style="color: green;font-weight: bold;">SELECIONADO</span>
					</c:if>
					<c:if test="${!interesse.selecionado}">
					    Aguardando Seleção
					</c:if>
				</td>			
				<td style="width: 1px;">
					<c:if test="${ interesse.idArquivoCurriculo > 0}">
						<a href="${ctx}/verArquivo?idArquivo=${ interesse.idArquivoCurriculo }&key=${ sf:generateArquivoKey(interesse.idArquivoCurriculo) }"
							 id="verCurriculo" target="_blank" title="Visualizar Currículo">
							<h:graphicImage  value="/img/report.png"/>
						</a>
					</c:if>			
				</td>	
				<td style="width: 1px;">
					<h:commandLink target="_BLANK" action="#{interesseOfertaMBean.visualizarLattes}" title="Visualizar Currículo Lattes" id="verCurriculoLattes"
						rendered="#{ not empty interesse.discente.perfil.enderecoLattes }">
						<h:graphicImage  value="/img/prodocente/lattes.gif"/>
						<f:setPropertyActionListener value="#{interesse}" target="#{interesseOfertaMBean.obj}"/>
					</h:commandLink>																	
				</td>
				<td style="width: 1px;">
					<h:commandLink action="#{interesseOfertaMBean.selecionarInteressado}" title="Selecionar Estagiário" rendered="#{!interesse.selecionado && interesseOfertaMBean.permiteSelecionaDiscente}">
						<h:graphicImage value="/img/estagio/novo_estagiario.png"/>
						<f:setPropertyActionListener value="#{interesse}" target="#{interesseOfertaMBean.obj}"/>
					</h:commandLink>								
				</td>
			</tr>
			<tr class="${loop.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<th style="font-weight: bold;">Perfil:</th>
				<td colspan="4">${interesse.descricaoPerfil}</td>
				<td></td>
			</tr>							
		</c:forEach>
		<tfoot>
			<tr>
				<td colspan="7" align="center">
					<h:commandButton value="<< Voltar" action="#{ofertaEstagioMBean.telaLista}" id="btVoltar"/>
				</td>
			</tr>
		</tfoot>	
	</table>	
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>