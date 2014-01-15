<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2 class="tituloPagina">
	<ufrn:subSistema></ufrn:subSistema> &gt;
	<c:out value="Relatório Anual de Projeto de Pesquisa"/>
</h2>

<div id="ajuda" class="descricaoOperacao">
		<p>
			<strong>Bem-vindo ao Cadastro de Relatório Anual de Projeto de Pesquisa</strong>
		</p>
		<br/>
		<p>
			O relatório anual de projeto deve ser submetido na forma de um <strong>Resumo Expandido</strong> contendo no máximo 10.000 caracteres.
		</p>	
		<p>
			A cada 5 minutos surgirá um alerta perguntando se você deseja salvar o relatório. 
			Clicando em <strong>SIM</strong> o relatório será salvo automaticamente.
		</p>	
		<p>
			Ao concluir o preenchimento, clique no botão <strong>Submeter Relatório</strong>. 
			Você ainda poderá editar o texto do resumo expandido até a data de encerramento do período de submissão dos relatórios anuais de projetos. 
		</p>	
</div>

<html:form action="/pesquisa/relatorioProjeto" method="post" focus="obj.resumo" styleId="form">
	<html:hidden property="obj.id" />
	<html:hidden property="obj.projetoPesquisa.id" />
	<html:hidden property="obj.projetoPesquisa.interno" />

	<table class="formulario" width="98%">
	<caption> Relatório Anual </caption>
	<tbody>
	    <tr>
			<td>
				<b>Projeto de Pesquisa:</b>
			</td>
	    </tr>
		<tr>
			<td>
				${formRelatorioProjeto.obj.projetoPesquisa.codigoTitulo}
			</td>
		</tr>
		<tr>
			<td> <b>Descrição do Projeto:</b> </td>
		</tr>
		<tr>
			<td> ${formRelatorioProjeto.obj.projetoPesquisa.descricao} </td>
		</tr>
	    <tr>
			<td> <b>Resumo Expandido:</b> </td>
	    </tr>
		<tr>
			<td style="padding: 5px 12px;">
				<ufrn:textarea property="obj.resumo" rows="20" style="width: 99%" maxlength="10000"/>
			</td>
		</tr>
	</tbody>
	<tfoot>
		<tr><td>
			<html:button dispatch="preEnviar"> Submeter Relatório </html:button>
			<html:button dispatch="cancelar" cancelar="true">Cancelar </html:button>
    	</td></tr>
	</tfoot>
	</table>
</html:form>

<script type="text/javascript">
	function salvar() {
		input = confirm('Deseja Gravar (Rascunho) agora?');
		if (input){			
			document.forms[0].action='relatorioProjeto.do?dispatch=preGravar'
			document.forms[0].submit();
		}
	}
	setInterval('salvar()',300000);
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>