<div id="abas-objetivo">
	<div id="objetivo-selecao" class="aba">
		<p class="ajuda">
			Utilize o espaço abaixo para definir o Objetivo do programa. 
				<h:graphicImage url="/img/required.gif" style="vertical-align: top;" />
		</p>
		<h:inputTextarea value="#{programaComponente.obj.objetivos}" id="objetivo" rows="10" cols="110" />
	</div>

	<div id="conteudo-selecao" class="aba">
		<p class="ajuda">
			Utilize o espaço abaixo para definir o Conteúdo do programa.
			<h:graphicImage url="/img/required.gif" style="vertical-align: top;" />
		</p>
		<h:inputTextarea value="#{programaComponente.obj.conteudo}" id="conteudo" rows="10" cols="110" />
	</div>
	
	<div id="habilidade-selecao" class="aba">
		<p class="ajuda">
			Utilize o espaço abaixo para definir a Competência e as Habilidades do programa.
			<h:graphicImage url="/img/required.gif" style="vertical-align: top;" />
		</p>
		<h:inputTextarea value="#{programaComponente.obj.competenciasHabilidades}" id="competencias" rows="10" cols="110" />
	</div>
</div>
<%-- 
<script>
var Abas = {
    init : function(){
        var abas = new YAHOO.ext.TabPanel('abas-objetivo');
        abas.addTab('objetivo-selecao', "Objetivos");
        abas.addTab('conteudo-selecao', "Conteudo");
        abas.addTab('habilidade-selecao', "Competencias e Habilidades");
        abas.activate('objetivo-selecao');
    }
};
YAHOO.ext.EventManager.onDocumentReady(Abas.init, Abas, true);
</script>
--%>