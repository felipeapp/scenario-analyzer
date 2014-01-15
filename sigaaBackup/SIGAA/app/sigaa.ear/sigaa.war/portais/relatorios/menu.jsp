<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" charset="ISO-8859">
	var J = jQuery.noConflict();
</script>

<style>
	#abas {
		width: 100%;
		background:  #C4D2EB; 
	}
	
	#abas td {
		width:25%; 
		text-align:center;
		cursor: pointer;
		font-family:verdana,arial,freesans,helvetica;
		font-weight: bold;
		color: #003395;
		font-variant: small-caps;	
	}
	
	.clear { clear: both; }
				
</style>
<f:view>
	<h:form>

	<h2>Portal de Relatórios de Gestão</h2>			
		
	<table width="100%" style="border:1px solid #C4D2EB;">
		<tr>
			<td>
				<table id="abas" border="1px" bordercolor="#EFF3FA">
					<tr>
						<td id="#tab_ensino"><h:graphicImage width="25px" value="/img/icones/ensino_menu.gif"/><br/>Ensino</td>
						<td id="#tab_pesquisa"><h:graphicImage width="25px" value="/img/icones/pesquisa_menu.gif"/><br/>Pesquisa</td>
						<td id="#tab_extensao"><h:graphicImage width="25px" value="/img/icones/extensao_menu.gif"/><br/>Extensão</td>
						<td id="#tab_relatorios_estatisticos"><h:graphicImage width="25px" value="/img/icones/producao_menu.gif"/><br/>Relatórios Estatísticos</td>
					</tr>
				</table>			
			</td>
		</tr>
		<tr>		
			<td  id="operacoes-subsistema"  class="reduzido" style="background:#FAFDFF">
				<div id="tab_ensino" class="contaba">
			        <%@include file="/portais/relatorios/abas/ensino.jsp"%>
			        <div class="clear"></div>
				</div>
		
				<div id="tab_pesquisa" class="contaba">
					<%@include file="/portais/relatorios/abas/pesquisa.jsp"%>
					<div class="clear"></div>
		        </div>
		
				<div id="tab_extensao" class="contaba">
					<%@include file="/portais/relatorios/abas/extensao.jsp"%>
					<div class="clear"></div>
				</div>
				
				<div id="tab_relatorios_estatisticos" class="contaba">
					<%@include file="/portais/relatorios/abas/relatorios_estatisticos.jsp"%>
					<div class="clear"></div>
				</div>
			</td>
		</tr>
	</table>
	
	<script>
		J(function(){
			// oculta todas as abas
			J("div.contaba").hide();
	
			// mostra somente  a primeira aba
			J("div.contaba:first").show(100);
	
			// seta a primeira aba como selecionada (na lista de abas)
			J("#abas td:first").addClass("selected");
			
			// quando clicar no link de uma aba
			J("#abas td").click(function(){
	
				// Se começa com #tab_, é uma aba. Então abre o painel.
				var link = /^#tab_.*/;
				if (link.test(J(this).attr("id"))){
	
					// oculta todas as abas
					J("div.contaba").hide();

					// tira a seleção da aba atual
					J("#abas td").removeClass("selected");
	
					// adiciona a classe selected na selecionada atualmente
					J(this).parent().addClass("selected");
	
					// mostra a aba clicada
					J(J(this).attr("id")).show(100);
					
					return false;
				}
			});
		});
	</script>
	
	</h:form>
</f:view>

<div class="linkRodape">
    <html:link action="/verMenuPrincipal">Menu Principal</html:link>
</div>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>