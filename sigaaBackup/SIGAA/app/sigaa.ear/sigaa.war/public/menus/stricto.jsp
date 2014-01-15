<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>

<dl>
	<dt>
		<div class="opcao curso-stricto">
		<h3>Cursos</h3>
		<a href="/sigaa/public/curso/lista.jsf?nivel=S&aba=p-stricto">Consulte os cursos de Mestrado/Doutorado oferecidos pela ${ configSistema['siglaInstituicao'] }.</a>
		</div>
	</dt>
	<dt>
		<div class="opcao componentes">
		<h3>Componentes Curriculares</h3>
		<a href="/sigaa/public/componentes/busca_componentes.jsf?nivel=S&aba=p-stricto">
			Consulte os detalhes e os programas dos componentes curriculares ministrados nos cursos de graduação da ${ configSistema['siglaInstituicao'] }.
		</a>
		</div>
	</dt>
	<dt>
		<div class="opcao selecao">
		<h3>Processos Seletivos  </h3>
		<a href="/sigaa/public/processo_seletivo/lista.jsf?nivel=S&aba=p-stricto">
			Consulte os processos seletivos abertos, inscreva-se ou gerencie suas inscrições.
		</a>
		</div>
	</dt>
	<dt>
		<div class="opcao programas">
		<h3>Programas de Pós-Graduação</h3>
		<a href="/sigaa/public/programa/lista.jsf?aba=p-stricto">Conheça os programas de pós-graduação da ${ configSistema['siglaInstituicao'] }.</a>
		</div>
	</dt>

</dl>