<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style>

	#tabela {
		margin-top: 50px; 
		width: ;		
	}

</style>
<f:view>
<div>
<c:choose>
<c:when test="${not empty atividadeExtensao.atividadesLocalizadas}">
	<h2> Relatório com dados de contato com Coordenadores de Projetos </h2>

<div id="parametrosRelatorio">
<table>
	<c:if test="${atividadeExtensao.checkBuscaTitulo}">
		<tr>
			<th>Título da Ação:</th>
			<td>${atividadeExtensao.buscaNomeAtividade}</td>
		</tr>
	</c:if>

	<c:if test="${atividadeExtensao.checkBuscaAno}">
		<tr>
			<th>Ano:</th>
			<td>${atividadeExtensao.buscaAno}</td>
		</tr>
	</c:if>
	<c:if test="${atividadeExtensao.checkBuscaCodigo}">
		<tr>
			<th>Código:</th>
			<td>${atividadeExtensao.buscaCodigo}</td>
		</tr>
	</c:if>

	<c:if test="${atividadeExtensao.checkBuscaPeriodo}">
		<tr>
			<th>Período:</th>
			<td><h:outputText value="#{atividadeExtensao.buscaInicio}">
				<f:convertDateTime pattern="dd/MM/yyyy" />
			</h:outputText> 
			a <h:outputText value="#{atividadeExtensao.buscaFim}">
				<f:convertDateTime pattern="dd/MM/yyyy" />
			</h:outputText></td>
		</tr>
	</c:if>

	<c:if test="${atividadeExtensao.checkBuscaEdital}">
		<tr>
			<th>Edital:</th>
			<td>${atividadeExtensao.nomeEdital}</td>
		</tr>
	</c:if>

	<c:if test="${atividadeExtensao.checkBuscaTipoAtividade}">
		<tr>
			<th>Tipo da Ação:</th>
			<td>${atividadeExtensao.nomeTipoAcao}</td>
		</tr>
	</c:if>

	<c:if test="${atividadeExtensao.checkBuscaAreaCNPq}">
		<tr>
			<th>Área do CNPq:</th>
			<td>${atividadeExtensao.nomeAreaCnpq}</td>
		</tr>
	</c:if>

	<c:if test="${atividadeExtensao.checkBuscaUnidadeProponente}">
		<tr>
			<th>Unidade Proponente:</th>
			<td>${atividadeExtensao.nomeUnidade}</td>
		</tr>
	</c:if>

	<c:if test="${atividadeExtensao.checkBuscaCentro}">
		<tr>
			<th>Centro da Ação:</th>
			<td>${atividadeExtensao.nomeCentro}</td>
		</tr>
	</c:if>

	<c:if test="${atividadeExtensao.checkBuscaAreaTematicaPrincipal}">
		<tr>
			<th>Área Temática:</th>
			<td>${atividadeExtensao.nomeArea}</td>
		</tr>
	</c:if>

	<c:if test="${atividadeExtensao.checkBuscaServidor}">
		<tr>
			<th>Servidor:</th>
			<td>${atividadeExtensao.membroEquipe.servidor.pessoa.nome}</td>
		</tr>
	</c:if>
	

	<c:if test="${acesso.extensao}">

		<c:if test="${atividadeExtensao.checkBuscaSituacaoAtividade}">
			<tr>
				<th>Situação da Ação:</th>
				<td>${atividadeExtensao.nomeSituacao}</td>
			</tr>
		</c:if>

		<c:if test="${atividadeExtensao.checkBuscaRegistroSimplificado}">
			<tr>
				<th>Tipo Registro:</th>
				<td>${atividadeExtensao.nomeTipoRegistro}</td>
			</tr>
		</c:if>

		<c:if test="${atividadeExtensao.checkBuscaFinanciamentoConvenio}">
			<tr>
				<th valign="top">Financiamentos & Convênios:</th>
				<td><c:if test="${atividadeExtensao.buscaFinanciamentoInterno}">
				Financiamento Interno <br />
				</c:if> <c:if test="${atividadeExtensao.buscaFinanciamentoExterno}">
				Financiamento Externo <br />
			</c:if> <c:if test="${atividadeExtensao.buscaAutoFinanciamento}">
				Auto Financiamento <br />
			</c:if> <c:if test="${atividadeExtensao.buscaConvenioFunpec}">
				Convênio Funpec <br />
			</c:if></td>
			</tr>

		</c:if>

	</c:if>

</table>
</div>


<table class="tabelaRelatorio" width="100%">

		<c:forEach var="item" items="#{atividadeExtensao.atividadesLocalizadas}">
		<tr>
			<td>	
				<br/><b>${ item.membroInfo.pessoa.nome }</b><hr/>
			<div><i> <c:if
				test="${item.membroInfo.pessoa.telefone != null}"> Telefone: ${item.membroInfo.pessoa.telefone} </c:if>
			<c:if test="${item.membroInfo.pessoa.telefone == null}"> Telefone não informado. </c:if>

			<c:if test="${item.membroInfo.pessoa.celular != null}"> Celular: ${item.membroInfo.pessoa.celular} </c:if>
			<c:if test="${item.membroInfo.pessoa.celular == null}"> Celular não informado. </c:if>

			<c:if test="${item.membroInfo.pessoa.email != null}"> Email: ${item.membroInfo.pessoa.email} </c:if>
			<c:if test="${item.membroInfo.pessoa.email == null}"> Email não informado. </c:if>

			</i></div>

			<div style="text-indent: 70px;">
				${ item.codigo } ${ item.titulo }
			</div>
				
			</td>
			
		</tr>
		</c:forEach>
		
	</table>
						
	<br/>
	</c:when>
	<c:otherwise>
		<center><i> Nenhuma ação de extensão localizada </i></center>
	</c:otherwise>
</c:choose>
</div>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>