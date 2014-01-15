<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<c:set var="confirmRemover" value="return confirm('Tem certeza que deseja remover este questionário?');" scope="request"/>


<style>
	.rich-table, .rich-table-header,.rich-table-headercell, .rich-table-cell,
	  .rich-subtable-cell, .rich-table-footercell, .rich-subtable-footercell {
		 border-width:0;
	}

	span.radio, span.checkbox {
		padding: 5px 2px 5px 32px;
		
	}

	.pergunta{
		font-weight: bold;
		padding-top: 10px;
		display: block;
	}

	span.radio {
		background: url(/sigaa/img/questionario/radio.gif) no-repeat;
		background-position: 10px 1px;		
	}
	
	span.checkbox {
		background: url(/sigaa/img/questionario/checkbox.gif) no-repeat;
		background-position: 10px 1px;		
	}
	
	span.marcado {
		background-position: 10px -49px;
	}
	
</style>

<f:view>
<h2> <ufrn:subSistema/> &gt; Questionário > Dados do Questionário </h2>


<%@include file="/geral/questionario/_dados_gerais.jsp" %>

<table class="formulario" width="100%">
	<tr><td>
		<%@include file="/geral/questionario/_perguntas.jsp" %>
	</td></tr>
	
	<tfoot>
		<tr><td>
			<h:form>
				<h:commandButton value="#{ questionarioBean.confirmButton }" action="#{questionarioBean.remover}" onclick="#{confirmRemover}"/>
				<h:commandButton value="Cancelar" action="#{questionarioBean.cancelar}" onclick="#{confirm}"/>
			</h:form>
		</td></tr>
	</tfoot>
</table>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>