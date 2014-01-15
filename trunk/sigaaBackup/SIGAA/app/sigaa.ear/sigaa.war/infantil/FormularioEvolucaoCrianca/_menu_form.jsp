<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib uri="/tags/a4j" prefix="a4j"%>
<%@taglib uri="/tags/rich" prefix="rich"%>

<rich:contextMenu attached="false" id="menuFormulario" hideDelay="300">

    <rich:menuItem disabled="true" id="idTarefa">
        <b> Operacoes </b>
    </rich:menuItem>

	<rich:menuItem value="Adicionar Bloco " icon="/img/infantil/icons/bloco.png" action="#{ formularioEvolucaoCriancaMBean.adicionarItem }" id="addBloco" immediate="true">
		<f:param name="ordem" value="{ordem}"/>
		<f:param name="profundidade" value="0"/>
	</rich:menuItem>

	<rich:menuItem value="Adicionar Area" icon="/img/infantil/icons/area.png" action="#{ formularioEvolucaoCriancaMBean.adicionarItem }" id="addArea" immediate="true">
		<f:param name="ordem" value="{ordem}"/>
		<f:param name="profundidade" value="1"/>
	</rich:menuItem>

	<rich:menuItem value="Adicionar Conteudo" icon="/img/infantil/icons/conteudo.png" action="#{ formularioEvolucaoCriancaMBean.adicionarItem }" id="addConteudo" immediate="true">
		<f:param name="ordem" value="{ordem}"/>
		<f:param name="profundidade" value="2"/>
	</rich:menuItem>

	<rich:menuItem value="Adicionar Sub-Conteudo" icon="/img/infantil/icons/subconteudo.png" action="#{ formularioEvolucaoCriancaMBean.adicionarItem }" id="addSubCon" immediate="true">
		<f:param name="ordem" value="{ordem}"/>
		<f:param name="profundidade" value="3"/>
	</rich:menuItem>

	<rich:menuItem value="Adicionar Objetivos" icon="/img/infantil/icons/objetivo.png" action="#{ formularioEvolucaoCriancaMBean.adicionarItem }" id="addObj" immediate="true">
		<f:param name="ordem" value="{ordem}"/>
		<f:param name="profundidade" value="4"/>
	</rich:menuItem>

	<rich:menuItem value="Subir Item" icon="/img/arrow_up.png" action="#{ formularioEvolucaoCriancaMBean.subirItem }" id="upItem" immediate="true">
		<f:param name="ordem" value="{ordem}"/>
	</rich:menuItem>

	<rich:menuItem value="Descer Item" icon="/img/arrow_down.png" action="#{ formularioEvolucaoCriancaMBean.descerItem }" id="downItem" immediate="true">
		<f:param name="ordem" value="{ordem}"/>
	</rich:menuItem>

	<rich:menuItem value="Salvar" icon="/img/salvar16.png" action="#{ formularioEvolucaoCriancaMBean.salvarItem }" id="salve" immediate="true">
		<f:param name="ordem" value="{ordem}"/>
	</rich:menuItem>

	<rich:menuItem value="Editar" icon="/img/edit.png" action="#{ formularioEvolucaoCriancaMBean.editarItem }" id="edit" immediate="true">
		<f:param name="ordem" value="{ordem}"/>
	</rich:menuItem>

	<rich:menuItem value="Remover" icon="/img/delete.gif" action="#{ formularioEvolucaoCriancaMBean.removerItem }" id="remover" immediate="true">
		<f:param name="ordem" value="{ordem}"/>
	</rich:menuItem>
	
</rich:contextMenu> 