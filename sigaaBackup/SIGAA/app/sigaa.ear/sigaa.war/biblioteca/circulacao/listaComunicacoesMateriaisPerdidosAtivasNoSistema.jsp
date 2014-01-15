<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style type="text/css">
	
	table.listagem tr.biblioteca td{
		background: #C8D5EC;
		font-weight: bold;
		padding-left: 20px;
	}

	table.listagem tr.nomeUsuario td{
		background: #EEEEEE;
		font-weight: bold;
		padding-left: 20px;
	}

	table.listagem td.detalhesComunicacao { display: none; padding: 0;}

</style>

<script type="text/javascript" >

function habilitarDetalhesComunicacao(idUsuarioBiblioteca, idEmprestimo) {

	var linha = 'linha_'+ idEmprestimo; // o id da linha da tabela
	var linhaImagem = 'imagem_' + idEmprestimo; // o id da imagem usada no link que abilita os detalhes
	
	if ( $(linha).style.display != 'table-cell' && $(linha).style.display != 'inline-block' ) {       //$() == getElementById()
		if ( !Element.hasClassName(linha, 'populado') ) {

			new Ajax.Request("/sigaa/biblioteca/circulacao/detalhesComunicacaoPerda.jsf?idEmprestimo=" + idEmprestimo+"&idUsuarioBiblioteca="+idUsuarioBiblioteca, {
				onComplete: function(transport) {
					$(linha).innerHTML = transport.responseText;
					Element.addClassName(linha, 'populado');
				}
			});
			
		}
		
		if (/msie/.test( navigator.userAgent.toLowerCase() )){
			$(linha).style.display = 'inline-block';
		}else{
			$(linha).style.display = 'table-cell';
		}
		
		
		$(linhaImagem).src= '/sigaa/img/biblioteca/cima.gif';
	} else {
		$(linha).style.display = 'none';
		$(linhaImagem).src= '/sigaa/img/biblioteca/baixo.gif';
	}
}

</script>

<f:view>

	<a4j:keepAlive beanName="buscaUsuarioBibliotecaMBean" />
	<a4j:keepAlive beanName="comunicarMaterialPerdidoMBean" />


	<h:form id="formulario">
		<h2><ufrn:subSistema /> &gt; Lista de Materiais Perdidos</h2>

		<div class="descricaoOperacao">
			<p>Esta tela lista todas as comunicações de perdas de materiais registradas no sistema, cujos empréstimos continuam ativos.</p>
		</div>


		<div class="infoAltRem" style="margin-top: 10px; width: 90%">
			<h:graphicImage value="/img/biblioteca/baixo.gif" style="overflow: visible;" />: 
			Mostar Detalhes da Comunicação da Perda do Material
	
			<h:graphicImage value="/img/biblioteca/cima.gif" style="overflow: visible;" />: 
			Ocultar Detalhes da Comunicação da Perda do Material <br/>
		</div>

		<table class="listagem" style="width: 90%" >
			<caption>Comunicações de Materiais Perdidos (${fn:length(comunicarMaterialPerdidoMBean.listaMateriaisPerdidos)})</caption>
			
			<thead>
				<tr>
					<th></th>
					<th style="width:60%">Código de Barras</th>
					<th style="text-align:center;">Prazo Original</th>
					<th style="text-align:center;">Prazo para Reposição</th>
				</tr>
			</thead>
			
			<tbody>
				
				<%-- Exibe as comunicações de perdas feitas para empréstimos ainda em aberto --%>
				
			
				<c:if test="${not empty comunicarMaterialPerdidoMBean.listaMateriaisPerdidos }">
						
					<c:set var="descricaoBiblioteca" value="" scope="request"/>
					
					<c:set var="nomeUsuario" value="" scope="request"/>
					
					<c:forEach items="#{comunicarMaterialPerdidoMBean.listaMateriaisPerdidos}" var="lmp" varStatus="status">

						<c:if test="${ requestScope.descricaoBiblioteca != lmp[6]}">
							<c:set var="descricaoBiblioteca" value="${lmp[6]}" scope="request"/>
							<tr class="biblioteca">
								<td colspan="4">${lmp[6]}</td>
							</tr>
						</c:if>

						<c:if test="${ requestScope.nomeUsuario != lmp[5]}">
							<c:set var="nomeUsuario" value="${lmp[5]}" scope="request"/>
							<tr class="nomeUsuario">
								<td colspan="4">Usuário(a): ${lmp[5]}</td>
							</tr>
						</c:if>

						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
							
							<td width="2%" style="text-align:center">
								<a href="javascript: void(0);" onclick="habilitarDetalhesComunicacao(${lmp[1]}, ${lmp[0]});">
									<img id="imagem_${lmp[1]}" src="${ctx}/img/biblioteca/baixo.gif"/>
								</a>
		
							</td>
							
							<td>
								${lmp[2]}
							</td>
						
							<td style="text-align:center;">
								<ufrn:format type="data" valor="${lmp[3]}" />
							</td>
							<td style="text-align:center;">
								<ufrn:format type="data" valor="${lmp[4]}" />
							</td>
							
						</tr>
						
						<%-- A linha da tabela que mostra os detalhes da comunicaçaõ--%>
						<tr class="${loop.index % 2 == 0 ? "linhaPar" : "linhaImpar"}"> 
							<td colspan="4" id="linha_${lmp[0]}" class="detalhesComunicacao" ></td>
						</tr>
						
					</c:forEach>
				</c:if>
				
				
				<c:if test="${empty comunicarMaterialPerdidoMBean.listaMateriaisPerdidos }">
						<tr>
							<td colspan="4" style="text-align:center; color:#FF0000;">Não existem nenhum comunicação de perda de material registrada para a sua biblioteca.</td>
						</tr>	
				</c:if>
				
				
			</tbody>
			
			<tfoot>
				<tr>
					<td colspan="4" align="center">
						<h:commandButton value="<< Voltar" action="#{comunicarMaterialPerdidoMBean.cancelar}" id="cmdVoltar"/>
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{comunicarMaterialPerdidoMBean.cancelar}" immediate="true" id="cancelar" />
					</td>
				</tr>
			</tfoot>
	
		</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>