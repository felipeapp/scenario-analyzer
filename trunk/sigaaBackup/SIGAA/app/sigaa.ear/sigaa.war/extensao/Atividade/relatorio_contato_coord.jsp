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
	<h2> Relat�rio com dados de contato com Coordenadores de Projetos </h2>

<div id="parametrosRelatorio">
<table>
	<c:if test="${atividadeExtensao.checkBuscaTitulo}">
		<tr>
			<th>T�tulo da A��o:</th>
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
			<th>C�digo:</th>
			<td>${atividadeExtensao.buscaCodigo}</td>
		</tr>
	</c:if>

	<c:if test="${atividadeExtensao.checkBuscaPeriodo}">
		<tr>
			<th>Per�odo:</th>
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
			<th>Tipo da A��o:</th>
			<td>${atividadeExtensao.nomeTipoAcao}</td>
		</tr>
	</c:if>

	<c:if test="${atividadeExtensao.checkBuscaAreaCNPq}">
		<tr>
			<th>�rea do CNPq:</th>
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
			<th>Centro da A��o:</th>
			<td>${atividadeExtensao.nomeCentro}</td>
		</tr>
	</c:if>

	<c:if test="${atividadeExtensao.checkBuscaAreaTematicaPrincipal}">
		<tr>
			<th>�rea Tem�tica:</th>
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
				<th>Situa��o da A��o:</th>
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
				<th valign="top">Financiamentos & Conv�nios:</th>
				<td><c:if test="${atividadeExtensao.buscaFinanciamentoInterno}">
				Financiamento Interno <br />
				</c:if> <c:if test="${atividadeExtensao.buscaFinanciamentoExterno}">
				Financiamento Externo <br />
			</c:if> <c:if test="${atividadeExtensao.buscaAutoFinanciamento}">
				Auto Financiamento <br />
			</c:if> <c:if test="${atividadeExtensao.buscaConvenioFunpec}">
				Conv�nio Funpec <br />
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
			<c:if test="${item.membroInfo.pessoa.telefone == null}"> Telefone n�o informado. </c:if>

			<c:if test="${item.membroInfo.pessoa.celular != null}"> Celular: ${item.membroInfo.pessoa.celular} </c:if>
			<c:if test="${item.membroInfo.pessoa.celular == null}"> Celular n�o informado. </c:if>

			<c:if test="${item.membroInfo.pessoa.email != null}"> Email: ${item.membroInfo.pessoa.email} </c:if>
			<c:if test="${item.membroInfo.pessoa.email == null}"> Email n�o informado. </c:if>

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
		<center><i> Nenhuma a��o de extens�o localizada </i></center>
	</c:otherwise>
</c:choose>
</div>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>