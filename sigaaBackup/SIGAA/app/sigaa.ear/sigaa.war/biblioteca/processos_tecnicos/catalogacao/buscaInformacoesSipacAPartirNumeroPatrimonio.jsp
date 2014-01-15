<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<script type="text/javascript" src="/sigaa/javascript/biblioteca/functions.js"> </script>

<h2>  <ufrn:subSistema /> &gt; Busca de Materiais Institucionais no Patrimônio da ${ configSistema['siglaInstituicao'] }</h2>

<div class="descricaoOperacao"> 
    
    <p> Caro(a) usuário(a),</p>
    
    <p> Por essa busca são recuperadas as informações dos materiais tombados que serão incluídos no acervo.</p>
    <br/>
    <p>A busca é realizada a partir de algum número do tombamento. 
         São recuperados todos os bens tombados para o mesmo termo de responsabilidade do mesmo material do bens cujo número do tombamento 
         foi digitado.
    </p>
    <p> <span style="font-style: italic;"> Podem ser tombados bens de diferentes materiais em um mesmo termo de responsabilidade. </span> </p>
    <br/>
    
    <p>Nos próximos passos pode-se escolher quais dos materiais tombados devem ser incluídos no acervo. 
    O sistema verifica se o tombamento foi realizado para a unidade da biblioteca onde os materiais estão sendo incluídos 
    e solicita uma confirmação caso se esteja incluindo no acervo materiais para bibliotecas diferentes das bibliotecas para que 
    eles foram tombados originalmente.  </p>
    

</div>


<style>
	.colunaTextoLargo{
		text-align:left;
		width: 40%;
	}
	
	.colunaTextoPequeno{
		text-align:left;
		width: 19%;
	}
	
	.colunaCentralizada{
		text-align:center;
	}
	
	.colunaIcone{
		text-align:center;
		width: 1%;
	}
	
	.colunaTextoCabecalho{
		text-align:left;
	}
	
	
</style>


<script type="text/javascript" src="/sigaa/javascript/biblioteca/functions.js" ></script>	
	
	
<f:view>
	
	
	<h:form id="formBuscaSipac">

		<a4j:keepAlive beanName="catalogacaoMBean"></a4j:keepAlive>
	
		<table width="80%" class="formulario">
			<caption>Entre com o número de patrimônio de um material </caption>
		
			<tr>
				<%--
				<td width="2%">
					<input type="radio" name="radioTipoBuscaSipac" value="buscarNumeroPatrimonio" id="radioNumeroPatrimonio"  ${catalogacaoMBean.buscandoNumeroPatrimonio ? 'checked="checked"' : '' } />			
				</td>
				--%>
				<th>Número do Patrimônio: </th>	
				<td colspan="5"> 
					<h:inputText value="#{catalogacaoMBean.numeroPatrimonio}" id="inputNumeroPatrimonio"
					onkeyup="return formatarInteiro(this);" maxlength="10" 
					onkeypress="return executaClickBotao(event, 'formBuscaSipac:botaoBuscaPatrimonio')"
					onchange="marcarCheckBox(this, 'radioNumeroPatrimonio'); document.getElementById('formBuscaSipac:inputNumeroTermoResponsabilidade').value = '';" /> 
				</td>
			</tr>
		
		
			<%-- Não dá para busca por termo de responsabilidade porque num mesmo termo podem ser tombados bens de 
			livros diferentes ai complica para eles escolherem em qual Título incluir. 
			<tr>
				<td width="2%">
					<input type="radio" name="radioTipoBuscaSipac" value="buscarTermoResponsabilidade" id="radioTermoResponsabilidade" ${! catalogacaoMBean.buscandoNumeroPatrimonio ? 'checked="checked"' : '' } />			
				</td>
				
				<th>Número do Termo de Responsabilidade: </th>	
				<td colspan="2"> 
					<h:inputText value="#{catalogacaoMBean.numeroTermoResponsabilidade}" id="inputNumeroTermoResponsabilidade"
					onkeyup="return formatarInteiro(this);" maxlength="10" 
					onkeypress="return executaClickBotao(event, 'formBuscaSipac:botaoBuscaPatrimonio')"
					onchange="marcarCheckBox(this, 'radioTermoResponsabilidade'); document.getElementById('formBuscaSipac:inputNumeroPatrimonio').value = ''; " /> 
				</td>
				
				<th>Ano: </th>	
				<td colspan="2"> 
					<h:inputText value="#{catalogacaoMBean.anoTermoResponsabilidade}" id="inputAnoTermoResponsabilidade"
					onkeyup="return formatarInteiro(this);" maxlength="4" 
					onkeypress="return executaClickBotao(event, 'formBuscaSipac:botaoBuscaPatrimonio')"
					onchange="marcarCheckBox(this, 'radioTermoResponsabilidade'); document.getElementById('formBuscaSipac:inputNumeroPatrimonio').value = '';"/> 
				</td>
				
			</tr>
			--%>
		
			<tfoot>
				<tr>		
					<td colspan="7">
						<h:commandButton id="botaoBuscaPatrimonio" value="Buscar" action="#{catalogacaoMBean.buscaInformacoesSipacAPartirNumeroPatrimonio}" />
						<h:commandButton value="Cancelar" onclick="#{confirm}" immediate="true" action="#{catalogacaoMBean.cancelar}" />
					</td>
				</tr>
				
			</tfoot>
		
		</table>
	
		<%@include file="/WEB-INF/jsp/include/_campos_obrigatorios.jsp"%>








		<%--   parte da págia com a resposta vinda so sipac sobre os títulos comprados daquela nota fiscal  --%>

		<t:div rendered="#{catalogacaoMBean.infoTituloCompra != null }">
			<table class="listagem" style="margin-top: 20px"> 
				<tr>
					<td colspan="10" style="text-align:center;color: #003395;font-variant: small-caps;font-weight: bold; padding-top:10px">
						<span style="color:black;"> Tipo de Tombamento:</span> ${catalogacaoMBean.infoTituloCompra.tipoTombamento}
					</td>			
				</tr>
				
				<tr>
					<td colspan="10" style="text-align:center;color: #003395;font-variant: small-caps;font-weight: bold; padding-top:10px">
						<span style="color:black;"> Bens Recuperados:</span> 
						${catalogacaoMBean.infoTituloCompra.primeiroNumeroPatrimonioRecuperado}
						a ${catalogacaoMBean.infoTituloCompra.ultimoNumeroPatrimonioRecuperado}
					</td>			
				</tr>
				
				<tr>
					<td colspan="10" style="text-align:center;color: #003395;font-variant: small-caps;font-weight: bold; padding-top:10px">
						<span style="color:black;"> Termo de Responsabilidade Nº:</span> 
						${catalogacaoMBean.infoTituloCompra.descricaoTermoResponsabibliodade}
					</td>			
				</tr>
				
				<c:if test="${infoTitulo.numeroMateriaisInformacionaisNaoUsados > 0}">
					<tr>
						<td colspan="10" style="text-align:center;color: #003395;font-variant: small-caps;font-weight: bold; padding-top:10px">
							Todos os materiais desse termo de responsabilidade foram incluídos no acervo
						</td>			
					</tr>
				</c:if>
				
			</table>
		</t:div>
	
		<t:div styleClass="infoAltRem" style="margin-top: 10px" rendered="#{catalogacaoMBean.infoTituloCompra != null }">
	
			<h:graphicImage value="/img/biblioteca/carrinho_compras.png" style="overflow: visible;" />: 
			Total de Bens Tombados
			
			<h:graphicImage value="/img/cross.png" style="overflow: visible;" />: 
			Bens Tombados que não estão no Acervo
			
			<h:graphicImage value="/img/livro.png" style="overflow: visible;" />: 
			Bens Tombados que já estão no Acervo
			
			<br/>
			
			<h:graphicImage value="/img/seta.gif" style="overflow: visible;"/>: 
			Pesquisar o Título
	
		</t:div> 
	
	
	
	
	
	
	
	
		<c:if test="${catalogacaoMBean.infoTituloCompra != null}">
		
			<table style="width: 100%" class="listagem">
				<caption> Informações do Tombamento recuperadas do ${ configSistema['siglaSipac'] } </caption>
				<thead>
					<tr>
						<td style="width: 34%">Autor</td>
						<td style="width: 45%">Título</td>
						<td style="width: 5%; text-align: center;">Ano</td>
						<td style="width: 5%; text-align: right;"><h:graphicImage value="/img/biblioteca/carrinho_compras.png" style="overflow: visible;"/></td>
						<td style="width: 5%; text-align: right;"><h:graphicImage value="/img/cross.png" style="overflow: visible;" /></td>
						<td style="width: 5%; text-align: right;"><h:graphicImage value="/img/livro.png" style="overflow: visible;" /></td>
						<td style="width: 1%"></td>
					</tr>	
				</thead>
				
				<tbody>
				
					
						<tr>
							<td>
								<h:outputText value="#{catalogacaoMBean.infoTituloCompra.autor}"/>
							</td>
							<td>
								<h:outputText value="#{catalogacaoMBean.infoTituloCompra.titulo}"/>
							</td>
							<td style="text-align: center;">
								<h:outputText value="#{catalogacaoMBean.infoTituloCompra.ano}"/>
							</td>
							<td style="text-align: right;">
								<h:outputText value="#{catalogacaoMBean.infoTituloCompra.numeroTotalMateriaisInformacionaisCompra}"/>
							</td>
							<td style="text-align: right;">
								<h:outputText value="#{catalogacaoMBean.infoTituloCompra.numeroMateriaisInformacionaisNaoUsados}"/>
							</td>
							<td style="text-align: right;">
								<h:outputText value="#{catalogacaoMBean.infoTituloCompra.numeroMateriaisInformacionaisAcervo}"/>
							</td>
							<td>
								<h:commandLink id="cmdLinkbuscaSugestaoTitulosAcervo" action="#{pesquisaTituloCatalograficoMBean.iniciarPesquisa}" 
											rendered="#{catalogacaoMBean.infoTituloCompra.numeroMateriaisInformacionaisNaoUsados > 0}">
									<h:graphicImage value="/img/seta.gif" style="overflow: visible;" title="Pesquisar o Título"/>
									
									<f:param name="isPesquisaTituloParaCatalogacaoComTombamento" value="true" />
									
									<f:setPropertyActionListener target="#{pesquisaTituloCatalograficoMBean.tituloSipac}" value="#{catalogacaoMBean.infoTituloCompra.titulo}" />
									<f:setPropertyActionListener target="#{pesquisaTituloCatalograficoMBean.autorSipac}" value="#{catalogacaoMBean.infoTituloCompra.autor}" />
								</h:commandLink>
							</td>
						</tr>
					
				</tbody>
				
			</table>
	
		</c:if> 

		

		<c:if test="${catalogacaoMBean.infoTituloCompra != null && catalogacaoMBean.infoTituloCompra.numeroMateriaisInformacionaisNaoUsados > 0}">
		
			<div style="text-align: center; margin-left: auto; margin-right:auto; width: 80%; margin-top: 30px;">
				
				
				<a id="linkMostarOcultarBensNaoUsados" onclick="mostrarOcultarDivNaoUsados();" style="color: #003395; cursor:pointer;">
					 <span id="spanLinkNaoUsados">Mostrar </span> Bens Tombados que ainda não estão no Acervo 
				</a>
			
				<t:div id="divInformacoesBensNaoUsados" 
					rendered="#{catalogacaoMBean.infoTituloCompra != null && catalogacaoMBean.infoTituloCompra.numeroMateriaisInformacionaisNaoUsados > 0}">
					
					<table class="listagem" style="width: 50%; margin-top: 20px;">
						<caption> Bens Tombados que ainda não estão no Acervo ( ${catalogacaoMBean.infoTituloCompra.numeroMateriaisInformacionaisNaoUsados} )  </caption>
						<c:forEach var="numerosNaoUsado" items="#{catalogacaoMBean.infoTituloCompra.listaNumerosPatrimonioNaoUsados}" varStatus="status">
							<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
								<td style="text-align: center;" >${numerosNaoUsado}</td>
							</tr>
						</c:forEach>
					</table>
				</t:div>
			
			</div>
			
		</c:if>
		
		<c:if test="${catalogacaoMBean.infoTituloCompra != null && catalogacaoMBean.infoTituloCompra.numeroTotalMateriaisInformacionaisCompra > 0}">
		
			<div style="text-align: center; margin-left: auto; margin-right:auto; width: 80%; margin-top: 30px;">
				
				
				<a id="linkMostarOcultarBensRecuperados" onclick="mostrarOcultarDivRecuperados();" style="color: #003395; cursor:pointer;"> 
					<span id="spanLinkRecuperados">Mostrar </span> Bens Tombados recuperados para o Termo de Responsabilidade 
				</a>
				
				<t:div id="divInformacoesBensRecuperados" 
					rendered="#{catalogacaoMBean.infoTituloCompra != null && catalogacaoMBean.infoTituloCompra.numeroTotalMateriaisInformacionaisCompra > 0}">
					
					<table class="listagem" style="width: 50%; margin-top: 20px;">
						<caption> Bens Recuperados no Termo de Responsabilidade ( ${catalogacaoMBean.infoTituloCompra.numeroTotalMateriaisInformacionaisCompra} ) </caption>
						<c:forEach var="numerosRecuperados" items="#{catalogacaoMBean.infoTituloCompra.listaNumerosPatrimonioRecuperados}" varStatus="status">
							<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
								<td style="text-align: center;" >${numerosRecuperados}</td>
							</tr>
						</c:forEach>
					</table>
				</t:div>
			
			</div>
			
		</c:if>
		
	</h:form>
	
</f:view>

<script type="text/javascript">		

function focarCampo() {
	document.getElementById('formBuscaSipac:inputNumeroPatrimonio').focus();
}


focarCampo();

<%--

<c:if test="${catalogacaoMBean.buscandoNumeroPatrimonio}" >
		document.getElementById('formBuscaSipac:inputNumeroPatrimonio').focus();
	</c:if>
	<c:if test="${! catalogacaoMBean.buscandoNumeroPatrimonio}" >
		document.getElementById('formBuscaSipac:inputNumeroTermoResponsabilidade').focus();
	</c:if>
--%>

window.onload = function() {
	mostrarOcultarDivNaoUsados();

	mostrarOcultarDivRecuperados();
};



// Java Scritp para mostrar ou ocultar a listagem de bens não incluídos no acervo //
function mostrarOcultarDivNaoUsados(){
	
	div  = document.getElementById('formBuscaSipac:divInformacoesBensNaoUsados');
	
	span = document.getElementById('spanLinkNaoUsados'); 

	if(div == null)
		return;
	
	if(div.style.display == 'none'){
		div.style.display = '';
		span.innerHTML = 'Ocultar'
	}else{
		div.style.display = 'none';
		span.innerHTML = 'Mostrar'
	}
}

//Java Scritp para mostrar ou ocultar a listagem de bens não incluídos no acervo //
function mostrarOcultarDivRecuperados(){
	
	div  = document.getElementById('formBuscaSipac:divInformacoesBensRecuperados');
	
	span = document.getElementById('spanLinkRecuperados'); 

	if(div == null)
		return;
	
	if(div.style.display == 'none'){
		div.style.display = '';
		span.innerHTML = 'Ocultar'
	}else{
		div.style.display = 'none';
		span.innerHTML = 'Mostrar'
	}
}


//função que executa o click no botao passado quando o usuário pressiona o enter
function executaClickBotao(evento, idBotao) {
	
	var tecla = "";
	if (isIe())
		tecla = evento.keyCode;
	else
		tecla = evento.which;

	if (tecla == 13){
		document.getElementById(idBotao).click();
		return false;
	}
	
	return true;
	
}	

// testa se é o IE ou não
function isIe() {
	return (typeof window.ActiveXObject != 'undefined');
}


</script>




<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>