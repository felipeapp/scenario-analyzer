<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

	<ul>
		<li>Cursos
		<ul>
			<li>
				<h:commandLink value="Cadastrar" action="#{cursoGrad.preCadastrar}" onclick="setAba('cadastro')" id="cursoCadastrar" >
					<f:param value="S" name="nivel" />
				</h:commandLink>
			</li>
			<li><h:commandLink value="Listar/Alterar" action="#{cursoGrad.listar}" onclick="setAba('cadastro')" id="cursoEditar" />
		</ul>
		</li>
	</ul>
	<ul>
		<li>Áreas de Concentração
		<ul>
			<li><h:commandLink value="Cadastrar" onclick="setAba('cadastro')"  action="#{areaConcentracao.preCadastrar}" id="cadastrarArea"/></li>
			<li><h:commandLink value="Buscar/Alterar" onclick="setAba('cadastro')"  action="#{areaConcentracao.listar}" id="listarArea"/></li>
		</ul>
		</li>
	</ul>
	<ul>
		<li>Linhas de Pesquisa
		<ul>
			<li><h:commandLink value="Cadastrar" onclick="setAba('cadastro')"  action="#{linhaPesquisa.preCadastrar}" id="cadastrarLinha" /></li>
			<li><h:commandLink value="Buscar/Alterar" onclick="setAba('cadastro')"  action="#{linhaPesquisa.listar}" id="editarLinha" /></li>
		</ul>
		</li>
	</ul>
	<ul>
		<li>Disciplinas / Atividades
		<ul>
			<li><h:commandLink value="Cadastrar" onclick="setAba('cadastro')"  action="#{componenteCurricular.preCadastrar}" id="cadastrarDisciplina"/></li>
			<li><h:commandLink value="Buscar/Alterar" onclick="setAba('cadastro')"  action="#{componenteCurricular.listar}" id="listarDisciplina"/></li>
		</ul>
		</li>
	</ul>
	<ul>
		<li>Estrutura Curricular
		<ul>
			<li><h:commandLink value="Cadastrar" onclick="setAba('cadastro')"  action="#{curriculo.preCadastrar}" id="cadastrarEstrutura">
				<f:param value="S" name="nivel" />
			</h:commandLink></li>
			<li><h:commandLink value="Buscar/Alterar" onclick="setAba('cadastro')"  action="#{curriculo.preListar}" id="listarEstrutura">
				<f:param value="S" name="nivel" />
			</h:commandLink></li>
		</ul>
		</li>
	</ul>
	<ul>
		<li>Recomendação do Programa
		<ul>
			<li><h:commandLink value="Cadastrar" onclick="setAba('cadastro')"  action="#{recomendacao.preCadastrar}" id="cadastrarRecomendacao"/></li>
			<li><h:commandLink value="Listar/Alterar" onclick="setAba('cadastro')"  action="#{recomendacao.listar}" id="listarRecomendacao"/></li>
		</ul>
		</li>
	</ul>
	<ul>
		<li>Processo Seletivo
		<ul>
			<li> <h:commandLink action="#{processoSeletivo.listar}" value="Gerenciar Processos Seletivos"  onclick="setAba('discente')" id="cadastrarProcSeletivo"/> </li>
		</ul>
		</li>
	</ul>	
	<ul>
		<li><h:commandLink value="Equipe de Docentes de um Programa" onclick="setAba('cadastro')"  action="#{equipePrograma.preCadastrar}" id="cadastrarEquipDocente"/></li>
	</ul>
	
	<ul>
		<li>Unidade
		<ul>
			<li><h:commandLink action="#{unidade.popularBuscaGeral}" value="Consultar Unidades" onclick="setAba('cadastro')" id="listarUnidade"/></li>
		</ul>
		</li>
	</ul>		