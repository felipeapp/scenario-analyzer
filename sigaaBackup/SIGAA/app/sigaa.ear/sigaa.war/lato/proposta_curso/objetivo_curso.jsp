<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<h2><ufrn:subSistema /> &gt; Dados Básico &gt; Proposta </h2>

<%@include file="include/_operacao.jsp"%>

<h:form>
	<table class="formulario" width="100%" border="1">
		<caption>Objetivos e Importância do Curso</caption>
		
			<tr>
				<td colspan="2">
				<div id="abas-descricao">
		
					<div id="justificativa-objetivos" class="aba">
						<h:graphicImage url="/img/required.gif" style="vertical-align: top;" />&nbsp;<i>Justificativa/Objetivos do Curso. </i><br /><br />
						<h:inputTextarea id="justificativa-objetivos" value="#{cursoLatoMBean.obj.propostaCurso.justificativa}" style="width: 95%" rows="10"/>
					</div>

					<div id="local-curso" class="aba">
						<i>Local do Curso </i><br /><br />
						<h:inputTextarea id="local" value="#{cursoLatoMBean.obj.propostaCurso.localCurso}" style="width: 95%" rows="10" />
					</div>

				</div>
				</td>
			</tr>
		
		
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton value="<< Voltar" action="#{ cursoLatoMBean.telaAnterior }" />
					<h:commandButton value="Cancelar" action="#{ cursoLatoMBean.cancelar }" onclick="#{confirm}"/> 
					<h:commandButton value="Avançar >>" action="#{ cursoLatoMBean.cadastrar }"/>
				</td>
			</tr>
		</tfoot>
	</table>
	
	<br />
	<center>
		<h:graphicImage url="/img/required.gif" style="vertical-align: top;" />
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	</center>
	<br />
	
</h:form>

<script>
var Abas = {
    init : function(){
        var abas = new YAHOO.ext.TabPanel('abas-descricao');
        abas.addTab('justificativa-objetivos', "Justificativa e Objetivo");
        abas.addTab('local-curso', "Local do Curso");
        abas.activate('justificativa-objetivos');
    }
};


YAHOO.ext.EventManager.onDocumentReady(Abas.init, Abas, true);
YAHOO.ext.EventManager.onDocumentReady(Abas2.init, Abas2, true);
</script>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>