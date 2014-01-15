<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2 class="tituloPagina">
	<ufrn:subSistema></ufrn:subSistema> &gt;
	<c:out value="Relat�rio Anual de Projeto de Pesquisa"/>
</h2>

<div id="ajuda" class="descricaoOperacao">
		<p>
			<strong>Bem-vindo ao Cadastro de Relat�rio Anual de Projeto de Pesquisa</strong>
		</p>
		<br/>
		<p>
			O relat�rio anual de projeto deve ser submetido na forma de um <strong>Resumo Expandido</strong> contendo no m�ximo 10.000 caracteres.
		</p>	
		<p>
			A cada 5 minutos surgir� um alerta perguntando se voc� deseja salvar o relat�rio. 
			Clicando em <strong>SIM</strong> o relat�rio ser� salvo automaticamente.
		</p>	
		<p>
			Ao concluir o preenchimento, clique no bot�o <strong>Submeter Relat�rio</strong>. 
			Voc� ainda poder� editar o texto do resumo expandido at� a data de encerramento do per�odo de submiss�o dos relat�rios anuais de projetos. 
		</p>	
</div>

<html:form action="/pesquisa/relatorioProjeto" method="post" focus="obj.resumo" styleId="form">
	<html:hidden property="obj.id" />
	<html:hidden property="obj.projetoPesquisa.id" />
	<html:hidden property="obj.projetoPesquisa.interno" />

	<table class="formulario" width="98%">
	<caption> Relat�rio Anual </caption>
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
			<td> <b>Descri��o do Projeto:</b> </td>
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
			<html:button dispatch="preEnviar"> Submeter Relat�rio </html:button>
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