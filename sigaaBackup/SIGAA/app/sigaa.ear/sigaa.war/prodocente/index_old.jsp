<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<f:view>

<h3><ufrn:subSistema /> > <ufrn:link action="/prodocente/index1.jsf" value="Ir para nova index"/></h3>

<!--  Scripts do YAHOO -->
<link rel="stylesheet" type="text/css"
	href="/shared/javascript/yui/tabview/assets/tabs.css">
<link rel="stylesheet" type="text/css"
	href="/shared//javascript/yui/tabview/assets/border_tabs.css">
<script type="text/javascript"
	src="/shared/javascript/yui/tabview-min.js"></script>

<script type="text/javascript">
var criarAbas = function() {
    var tabView = new YAHOO.widget.TabView('tabs-prodocente');
};
criarAbas();
</script>


<div id="tabs-prodocente" class="yui-navset">
<ul class="yui-nav">
	<li ${aba=='prograd'?'class=selected':''}><a href="#prograd"><em>ProGrad</em></a></li>
	<li ${aba=='prh'?'class=selected':''}><a href="#prh"><em>PRH</em></a></li>
	<li ${aba=='proex'?'class=selected':''}><a href="#proex"><em>PROEX</em></a></li>
	<li ${aba=='ppg'?'class=selected':''}><a href="#ppg"><em>P�s-Gradua��o</em></a></li>
	<li ${aba=='propesq'?'class=selected':''}><a href="#propesq"><em>PROPESQ</em></a></li>
	<li ${aba=='relatorios'?'class=selected':''}><a href="#adm"><em>Relat�rios</em></a></li>
	<li ${aba=='adm'?'class=selected':''}><a href="#adm"><em>Administra��o</em></a></li>
</ul>
<div class="yui-content">
<div id="prograd">
	<ul>
		<li> <ufrn:link action="/prodocente/atividades/Pet/lista.jsf" value="Pet" aba="prograd"/> </li>
		<li> <ufrn:link action="/prodocente/atividades/ClassificacaoPet/lista.jsf" value="Classifica��o de Pet" aba="prograd"/> </li>
		<li> <ufrn:link action="/prodocente/atividades/Monitoria/lista.jsf" value="Monitoria" aba="prograd"/> </li>
	</ul>
</div>

<div id="prh">
<br>
<ul>
	<li><ufrn:link action="/prodocente/atividades/TipoAfastamento/lista.jsf" value="Tipo de Afastamento" aba="prh"/></li>
	<li><ufrn:link action="/prodocente/atividades/CargoAdministrativo/lista.jsf" value="Cargo Administrativo" aba="prh"/></li>
	<li><ufrn:link action="/prodocente/atividades/Licenca/lista.jsf" value="Licen�a/Afastamento" aba="prh"/></li>

</ul>
</div>

<div id="proex">Atividades PROEX
<br>

<ul>
	<li>�rea Tem�tica*</li>
	<li><ufrn:link action="/prodocente/atividades/AtividadeExtensao/form.jsf" value="Atividade de Extens�o" aba="proex"/></li>
	<li>Benefici�rio de Atividade de Extens�o*</li>
	<li>Linha Program�tica*</li>
	<li><ufrn:link action="/prodocente/atividades/Orientacao/lista.jsf" value="Orienta��o" aba="proex"/></li>
	<li><ufrn:link action="/prodocente/atividades/OrientacaoProex/form.jsf" value="Orienta��o - Proex" aba="proex"/></li>
	<li><ufrn:link action="/prodocente/atividades/ParceriaAtividadeExtensao/form.jsf" value="Parceria de Atividade de Extens�o" aba="proex"/></li>
	<li><ufrn:link action="/prodocente/atividades/SituacaoAtividadeExtensao/form.jsf" value="Situa��o de Atividade de Extens�o" aba="proex"/></li>
	<li><ufrn:link action="/prodocente/atividades/TipoMembroAtivividadeExtensao/form.jsf" value="Tipo de Membro de Atividade de Extens�o" aba="proex"/></li>
	<li><ufrn:link action="/prodocente/atividades/TipoAtividadeExtensao/form.jsf" value="Tipo de Atividade de Extens�o" aba="proex"/></li>

</ul>

</div>

<div id="ppg">
	<ul>
		<li> Lato-Sensu
		<ul>
		<li> <ufrn:link action="/prodocente/atividades/Coordenacao/lista.jsf" value="Coordena��o Curso" aba="ppg"/></li>
		<li> <h:form>
				<h:commandLink value="Ensino Disciplinas Lato" action="#{ atividadeEnsino.listarLato }"/>
			</h:form>
			</li>
		<li> <h:form>
				<h:commandLink value="Ensino Disciplinas Resid�ncia M�dica" action="#{ atividadeEnsino.listarResidenciaMedica }"/>
			</h:form>
		<li>
			<h:form>
				<h:commandLink value="Orienta��o de Monografia - Resid�ncia M�dica" action="#{ teseOrientada.listarResidencia }"/>
			</h:form>
		 </li>
		</ul>
	</ul>

	<ul>
		<li>
		Strict-Sensu
		<ul>
		<li>
			 <h:form>
				<h:commandLink value="Atividade de Ensino" action="#{ atividadeEnsino.listar }"/>
			</h:form>
		</li>
		<li><h:form><h:commandLink value="Tese" action="#{teseOrientada.listar }"/></h:form></li>
		</ul>
	</ul>
</div>

<div id="propesq">
	<ul>
		<li> <h:form>
				<h:commandLink value="Inicia��o Cient�fica"  action="#{ iniciacaoCientifica.listar}"/>
			</h:form> </li>
		<li> <h:form>
				<h:commandLink value="Relat�rio de Pesquisa" action="#{ relatorio.listar }"/>
			</h:form> </li>
		<li>
			<h:form>
				<h:commandLink value="Chefia de Base de Pesquisa" action="#{ chefia.listar}"/>
			</h:form>
		</li>
	</ul>
</div>

<div id="relatorios">
<h:form>
	<ul>
		<li> <h:commandLink value="Relat�rio de todas as atividades" action="#{todaProducao.exibirOpcoes}"/> </li>
		<li> <h:commandLink value="Quantitativos de produ��o acad�mica" action="#{prodQuantitativo.verFormulario}"/> </li>
		<li> <h:commandLink value="Relat�rio de Avalia��o para Concess�o de Cotas" action="#{producao.verRelatorioCotas}"/> </li>
		<li> <h:commandLink value="Relat�rio de Produtividade Docente (Antigo GED)" action="#{producao.verRelatorioProgressao}"/> </li>
	</ul>
</h:form>
</div>

<div id="adm">

<br>
<b>Produ��es</b>
<ul>
	<li><ufrn:link action="/prodocente/producao/SubTipoArtistico/lista.jsf" value="SubTipo Artistico" aba="adm"/></li>
	<li><ufrn:link action="/prodocente/producao/TipoEvento/lista.jsf" value="Tipo de Evento" aba="adm"/></li>
	<li><ufrn:link action="/prodocente/producao/TipoInstancia/lista.jsf" value="Tipo de Inst�ncia" aba="adm"/></li>
	<li><ufrn:link action="/prodocente/producao/TipoMembroColegiado/lista.jsf" value="Tipo de Membro de Colegiado" aba="adm"/></li>
	<li><ufrn:link action="/prodocente/producao/TipoParticipacao/lista.jsf" value="Tipo de Participa��o" aba="adm"/></li>
	<li><ufrn:link action="/prodocente/producao/TipoParticipacaoOrganizacaoEventos/lista.jsf" value="Tipo de Organiza��o em Eventos" aba="adm"/></li>
	<li><ufrn:link action="/prodocente/producao/TipoParticipacaoSociedade/lista.jsf" value="Tipo de Participa��o em Sociedade" aba="adm"/></li>
	<li><ufrn:link action="/prodocente/producao/TipoPeriodico/lista.jsf" value="Tipo de Per�odico" aba="adm"/></li>
	<li><ufrn:link action="/prodocente/producao/TipoProducaoTecnologica/lista.jsf" value="Tipo de Produ��o Tecnol�gica" aba="adm"/></li>
	<li><ufrn:link action="/prodocente/producao/TipoRegiao/lista.jsf" value="Tipo de Regi�o" aba="adm"/></li>
</ul>
<br>
<b>Atividades</b>
<ul>
	<li>Tipo de Atividade de Extens�o**</li>
	<li><ufrn:link action="/prodocente/atividades/ProgramaResidenciaMedica/lista.jsf" value="Programa de Residencia Medica" aba="adm"/></li>
	<li><ufrn:link action="/prodocente/atividades/TipoBolsaProdocente/lista.jsf" value="Tipo de Bolsa" aba="adm"/></li>
	<li><ufrn:link action="/prodocente/atividades/TipoChefia/lista.jsf" value="Tipo de Chefia" aba="adm"/></li>
	<li>Tipo de Membro de Atividade de Extens�o**</li>
	<li><ufrn:link action="/prodocente/atividades/TipoOrientacao/lista.jsf" value="Tipo de Orienta��o" aba="adm"/></li>
	<li><ufrn:link action="/prodocente/atividades/TipoParecer/lista.jsf" value="Tipo de Parecer" aba="adm"/></li>
	<li><ufrn:link action="/prodocente/atividades/TipoQualificacao/lista.jsf" value="Tipo de Qualifica��o" aba="adm"/></li>
</ul>
<br>
<b>Relat�rios</b>
<ul>
	<li><ufrn:link action="prodocente/producao/relatorios/produtividade/ipi/seleciona_docente.jsf" value="C�lculo do Ipi para um docente" aba="adm"/></li>
	<li><ufrn:link action="prodocente/producao/relatorios/produtividade/cadastro/lista.jsf" value="Cadastro de Relat�rios" aba="adm"/></li>
</ul>


</div>
</div>
</div>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>